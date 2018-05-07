package fileforce.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

import com.google.gson.Gson;

import fileforce.Configuration.AppConfig;
import fileforce.Configuration.RabbitConfiguration;
import fileforce.Helper.ParserUtils;
import fileforce.MapperWorker.CommonMapperWorker;
import fileforce.Model.Request.CommonIndexRequest;
import fileforce.Model.Response.ContentVersionResponseWorker;
import fileforce.Model.Response.IndexServiceResponse;
import fileforce.Model.Response.MasterTableResponseWorker;

@Configuration
public class AsyncProcessWorker {
	public static final String GOOGLE_DRIVE = "GoogleDrive";
	public static final String ERROR_MSG = "ERROR_MSG";
	public static final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
	
	public static void main(String[] args) {
	    ctx.register(AppConfig.class);
	    ctx.refresh();
	    //AsyncProcessWorker classObj =  new AsyncProcessWorker();
	    AsyncProcessWorker.createIndexJobListener();
    }
	
	public static void createIndexJobListener(){
		final ApplicationContext rabbitConfig = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
        final ConnectionFactory rabbitConnectionFactory = rabbitConfig.getBean(ConnectionFactory.class);
        final Queue rabbitQueue = rabbitConfig.getBean(Queue.class);
        final MessageConverter messageConverter = new SimpleMessageConverter();

        // create a listener container, which is required for asynchronous message consumption.
        // AmqpTemplate cannot be used in this case
        final SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
        listenerContainer.setConnectionFactory(rabbitConnectionFactory);
        listenerContainer.setQueueNames(rabbitQueue.getName());
        
        CommonMapperWorker mapper = ctx.getBean(CommonMapperWorker.class);
        
        // set the callback for message handling
        listenerContainer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
            	final String str = (String) messageConverter.fromMessage(message);
            	System.out.println("Received from RabbitMQ: " + str);
            	Gson gson = new Gson(); 
            	final CommonIndexRequest commonRequest = gson.fromJson(str, CommonIndexRequest.class);
                // simply printing out the operation, but expensive computation could happen here
                
            	if(commonRequest.getPlatform() != null && commonRequest.getPlatform().getPlatformName().equalsIgnoreCase(GOOGLE_DRIVE)){
            		MasterTableResponseWorker mTableResponseObj = mapper.getMasterDataWorker(commonRequest.getSalesforce().getOrgId());
        			System.out.println("Received from RabbitMQ Schema Name : " + mTableResponseObj.getSchemaName());
        			if(mTableResponseObj.getSchemaName() != null){
        				Map<String, IndexServiceResponse> mapPlatformIdAndResponse = runIndexingForEveryFile(commonRequest, mTableResponseObj, mapper);
        				//TODO
        				//Call the Salesforce rest service to dump the data.
        				if(mapPlatformIdAndResponse != null){
        					List<IndexServiceResponse> lstIndexResponses = convertToResponseBody(mapPlatformIdAndResponse);
        					System.out.println("lstIndexResponses size  =======" + lstIndexResponses.size());
        				}
        			}
        		}
            }
        });

        // set a simple error handler
        listenerContainer.setErrorHandler(new ErrorHandler() {
            public void handleError(Throwable t) {
            	System.out.println("In Error Handler == " + t.getMessage());
            	t.printStackTrace();
            }
        });

        // register a shutdown hook with the JVM
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutting down Index Operation Worker");
                listenerContainer.shutdown();
            }
        });

        // start up the listener. this will block until JVM is killed.
        listenerContainer.start();
        System.out.println("Index Operation Worker started");
	}
	
	
	//method to prepare the final JSON body
	private static List<IndexServiceResponse> convertToResponseBody(Map<String, IndexServiceResponse> mapPlatformIdAndResponse){
		List<IndexServiceResponse> lstIndexResponses = new ArrayList<IndexServiceResponse>();
		for(String platformId : mapPlatformIdAndResponse.keySet()){
			lstIndexResponses.add(new IndexServiceResponse(platformId, 
															mapPlatformIdAndResponse.get(platformId).getErrorMessage(),
															mapPlatformIdAndResponse.get(platformId).getIndexedBody()));
		}
		return lstIndexResponses;
	}
	
	
	//method to fetch every file and parse it with common parser
	private static Map<String, IndexServiceResponse> runIndexingForEveryFile(CommonIndexRequest commonRequest, 
											MasterTableResponseWorker mTableResponseObj,
											CommonMapperWorker mapper){
		List<ContentVersionResponseWorker> listGoogleDriveFiles  = mapper.getContentVersionData(mTableResponseObj.getSchemaName());
		if(listGoogleDriveFiles != null && listGoogleDriveFiles.size() > 0){
			System.out.println("Size of the files from database : " + listGoogleDriveFiles.size());
			Map<String, IndexServiceResponse> mapPlatformIdAndResponse = ParserUtils.parsefiles(commonRequest, listGoogleDriveFiles);
			return mapPlatformIdAndResponse;
		}
		return null;
	}
}
