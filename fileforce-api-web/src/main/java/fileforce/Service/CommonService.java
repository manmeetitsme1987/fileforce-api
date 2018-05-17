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
import fileforce.Model.Response.IndexServiceResponse;
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
	
	public IndexServiceResponse runIndexRequest(CommonIndexRequest commonRequest){
		IndexServiceResponse obj = new IndexServiceResponse();
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
					IndexServiceResponse.Job tempObj = obj.new Job();
					tempObj.setJobId("Test");
					tempObj.setJobStatus("In Progress");
					tempObj.setMessage("Your request has been queued.");
					obj.setJobInfo(tempObj);
					
					amqpTemplate.convertAndSend(json);
				}else{
					//parse one file and respond synchronously
					if(commonRequest.getPlatform().getPlatformName().equalsIgnoreCase(GOOGLE_DRIVE)){
	            		MasterTableResponse mTableResponseObj = commonMapper.getMasterData(commonRequest.getSalesforce().getOrgId());
	        			System.out.println("Received from RabbitMQ Schema Name : " + mTableResponseObj.getSchemaName());
	        			if(mTableResponseObj.getSchemaName() != null){
	        				Map<String, IndexServiceResponse> mapPlatformIdAndResponse = runIndexingForEveryFile(commonRequest, mTableResponseObj);
	        				//Call the Salesforce rest service to dump the data.
	        				if(mapPlatformIdAndResponse != null){
	        					obj = mapPlatformIdAndResponse.get(commonRequest.getPlatform().getPlatform_file_id());
	        					System.out.println("obj obj  =======" + obj);
	        				}
	        			}
	        		}
				}
			}
    	}catch(Exception e){
    		System.out.println("Error ====" + e.getStackTrace());
    		System.out.println("Error ====" + e.getMessage());
    	}
		
		System.out.println("obj ====" + obj.getJobInfo().getMessage());
		return obj;
	}
	
	//method to fetch every file and parse it with common parser
	private Map<String, IndexServiceResponse> runIndexingForEveryFile(CommonIndexRequest commonRequest, 
											MasterTableResponse mTableResponseObj){
		ContentVersionResponse contentVersionFile  = commonMapper.getContentVersionData(commonRequest.getPlatform().getPlatform_file_id());
		System.out.println("Size of the files from database : " + contentVersionFile);
		if(contentVersionFile != null){
			Map<String, IndexServiceResponse> mapPlatformIdAndResponse = ParserUtilsWeb.parsefiles(commonRequest, contentVersionFile);
			return mapPlatformIdAndResponse;
		}
		return null;
	}
}
