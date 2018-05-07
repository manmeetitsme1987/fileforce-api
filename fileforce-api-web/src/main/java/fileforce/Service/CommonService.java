package fileforce.Service;

import java.util.Map;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import fileforce.Configuration.RabbitConfiguration;
import fileforce.Helper.ParserUtilsWeb;
import fileforce.Mapper.CommonMapper;
import fileforce.Model.Request.CommonIndexRequest;
import fileforce.Model.Response.ContentVersionResponse;
import fileforce.Model.Response.IndexJobResponse;
import fileforce.Model.Response.MasterTableResponse;

@Service
public class CommonService {
	@Autowired private CommonMapper commonMapper;
	@Autowired private Queue rabbitQueue;
	
	public static final String GOOGLE_DRIVE = "GoogleDrive";
	public static final String ERROR_MSG = "ERROR_MSG";
	
	public MasterTableResponse getMasterTableDataByOrgId(String orgId) {
		MasterTableResponse response = commonMapper.getMasterData(orgId);
		return response;
	}
	
	public IndexJobResponse runIndexRequest(CommonIndexRequest commonRequest){
		IndexJobResponse obj = new IndexJobResponse();
		try{
			if(commonRequest.getPlatform() != null){
				if(commonRequest.getPlatform().getPlatform_file_id().isEmpty()){
					//creating the RabbitMQ queue and delegating to worker dyno
					ApplicationContext context = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
			    	AmqpTemplate amqpTemplate = context.getBean(AmqpTemplate.class);
			    	System.out.println(amqpTemplate + "====" + rabbitQueue.getName());
			    	Gson gson = new Gson(); 
					String json = gson.toJson(commonRequest);
					System.out.println("Sent to RabbitMQ: " + json);
					amqpTemplate.convertAndSend(json);
					obj.setJobId("Test");
					obj.setJobStatus("Requested");
				}else{
					//parse one file and respond synchronously
					if(commonRequest.getPlatform().getPlatformName().equalsIgnoreCase(GOOGLE_DRIVE)){
	            		MasterTableResponse mTableResponseObj = commonMapper.getMasterData(commonRequest.getSalesforce().getOrgId());
	        			System.out.println("Received from RabbitMQ Schema Name : " + mTableResponseObj.getSchemaName());
	        			if(mTableResponseObj.getSchemaName() != null){
	        				Map<String, String> mapPlatformIdAndResponse = runIndexingForEveryFile(commonRequest, mTableResponseObj);
	        				if(mapPlatformIdAndResponse != null && mapPlatformIdAndResponse.containsKey(commonRequest.getPlatform().getPlatform_file_id())){
	        					obj.setIndexBodyResponse(mapPlatformIdAndResponse.get(commonRequest.getPlatform().getPlatform_file_id()));
	        				}else{
	        					obj.setErrorStatus("Error in Parsing");
	        					obj.setErrorMessage(mapPlatformIdAndResponse.get(ERROR_MSG));
	        				}
	        			}
	        		}
					obj.setJobId("Test");
					obj.setJobStatus("Completed");
				}
			}
    	}catch(Exception e){
    		System.out.println("Error ====" + e.getStackTrace());
    		System.out.println("Error ====" + e.getMessage());
    	}
		return obj;
	}
	
	//method to fetch every file and parse it with common parser
	private Map<String, String> runIndexingForEveryFile(CommonIndexRequest commonRequest, 
											MasterTableResponse mTableResponseObj){
		ContentVersionResponse contentVersionFile  = commonMapper.getContentVersionData(commonRequest.getPlatform().getPlatform_file_id());
		System.out.println("Size of the files from database : " + contentVersionFile);
		if(contentVersionFile != null){
			Map<String, String> mapPlatformIdAndResponse = ParserUtilsWeb.parsefiles(commonRequest, contentVersionFile);
			return mapPlatformIdAndResponse;
		}
		return null;
	}
}
