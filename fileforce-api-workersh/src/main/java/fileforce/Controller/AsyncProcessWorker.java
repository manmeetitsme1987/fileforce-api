package fileforce.Controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
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
import fileforce.Model.Response.IndexServiceResponseWorker;
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
        				Map<String, IndexServiceResponseWorker> mapPlatformIdAndResponse = runIndexingForEveryFile(commonRequest, mTableResponseObj, mapper);
        				//TODO
        				//Call the Salesforce rest service to dump the data.
        				if(mapPlatformIdAndResponse != null){
        					List<IndexServiceResponseWorker> lstIndexResponses = new ArrayList<IndexServiceResponseWorker>(mapPlatformIdAndResponse.values());
        					System.out.println("lstIndexResponses size  =======" + lstIndexResponses.size());
        					makeHTTPCallToSalesforce(lstIndexResponses, commonRequest);
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
	
	
	private static void makeHTTPCallToSalesforce(List<IndexServiceResponseWorker> lstIndexResponses, CommonIndexRequest commonRequest){
		getJWTAccessToken();
		HttpURLConnection connection = null;
		try{
			URL url = new URL(commonRequest.getSalesforce().getUpdateFilesURL());
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			//connection.setRequestProperty("Content-Length", lstIndexResponses.);
			connection.setRequestProperty("Content-Type", "application/json");
		    connection.setUseCaches(false);
		    connection.setDoOutput(true);
		    
		    OutputStream os = connection.getOutputStream();
		    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8"); 
		    
		    Gson gson = new Gson(); 
			String json = gson.toJson(lstIndexResponses);
			System.out.println(json + "=== converted JSON");
			
		    osw.write(json);
		    osw.flush();
		    osw.close();
		    os.close();  //don't forget to close the OutputStream
		    connection.connect();
		    
		    InputStream in = new BufferedInputStream(connection.getInputStream());
            String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            
            System.out.println(result + "=== converted JSON after call to Salesforce");
            in.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	
	private static void getJWTAccessToken(){
	    String header = "{\"alg\":\"RS256\"}";
	    String claimTemplate = "'{'\"iss\": \"{0}\", \"sub\": \"{1}\", \"aud\": \"{2}\", \"exp\": \"{3}\"'}'";

	    try {
	      StringBuffer token = new StringBuffer();
	      //Encode the JWT Header and add it to our string to sign
	      //token.append(Base64.encodeBase64URLSafeString(header.getBytes("UTF-8")));
	      token.append(Base64.encodeBase64URLSafeString(header.getBytes("UTF-8")));
	      //Separate with a period
	      token.append(".");

	      //Create the JWT Claims Object
	      String[] claimArray = new String[4];
	      claimArray[0] = "3MVG95NPsF2gwOiNgyqtZADg2uWtpzF0B4X5wc5j.ZX2VGvKWpmhEQp0U7Zhv2usa9bhY3lb6uGmLOZHLavbY";
	      claimArray[1] = "manmeetitsme1987+fileforce-techspike@gmail.com";
	      claimArray[2] = "https://login.salesforce.com";
	      claimArray[3] = Long.toString( ( System.currentTimeMillis()/1000 ) + 300);
	      MessageFormat claims;
	      claims = new MessageFormat(claimTemplate);
	      String payload = claims.format(claimArray);

	      //Add the encoded claims object
	      token.append(Base64.encodeBase64URLSafeString(payload.getBytes("UTF-8")));

	      //Load the private key from a keystore
	      KeyStore keystore = KeyStore.getInstance("JKS");
	      keystore.load(new FileInputStream("./static/00D1r000000TYde.jks"), "qwerqwer1".toCharArray());
	      PrivateKey privateKey = (PrivateKey) keystore.getKey("SelfSignedCert_10Apr2018_152118", "qwerqwer1".toCharArray());
	      
	      //Sign the JWT Header + "." + JWT Claims Object
	      Signature signature = Signature.getInstance("SHA256withRSA");
	      signature.initSign(privateKey);
	      signature.update(token.toString().getBytes("UTF-8"));
	      String signedPayload = Base64.encodeBase64URLSafeString(signature.sign());

	      //Separate with a period
	      token.append(".");

	      //Add the encoded signature
	      token.append(signedPayload);

	      System.out.println(token.toString());

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	  
	}
	
	//method to fetch every file and parse it with common parser
	private static Map<String, IndexServiceResponseWorker> runIndexingForEveryFile(CommonIndexRequest commonRequest, 
											MasterTableResponseWorker mTableResponseObj,
											CommonMapperWorker mapper){
		List<ContentVersionResponseWorker> listGoogleDriveFiles  = mapper.getContentVersionData(mTableResponseObj.getSchemaName());
		if(listGoogleDriveFiles != null && listGoogleDriveFiles.size() > 0){
			System.out.println("Size of the files from database : " + listGoogleDriveFiles.size());
			Map<String, IndexServiceResponseWorker> mapPlatformIdAndResponse = ParserUtils.parsefiles(commonRequest, listGoogleDriveFiles);
			return mapPlatformIdAndResponse;
		}
		return null;
	}
}
