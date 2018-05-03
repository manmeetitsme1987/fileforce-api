package fileforce.Service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import fileforce.Configuration.RabbitConfiguration;
import fileforce.Mapper.CommonMapper;
import fileforce.Model.Request.CommonIndexRequest;
import fileforce.Model.Response.MasterTableResponse;

@Service
public class CommonService {
	@Autowired
	private CommonMapper commonMapper;
	@Autowired private Queue rabbitQueue;
	
	public MasterTableResponse getMasterTableDataByOrgId(String orgId) {
		MasterTableResponse response = commonMapper.getMasterData(orgId);
		return response;
	}
	
	public String runIndexRequest(CommonIndexRequest commonRequest){
		//creating the RabbitMQ queue and delegating to worker dyno
		try{
	    	ApplicationContext context = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
	    	AmqpTemplate amqpTemplate = context.getBean(AmqpTemplate.class);
	    	System.out.println(amqpTemplate + "====" + rabbitQueue.getName());
	    	amqpTemplate.convertAndSend(rabbitQueue.getName(), commonRequest);
	    	
	        System.out.println("Sent to RabbitMQ: " + commonRequest);
	        // Send the bigOp back to the confirmation page for displaying details in view
	    	}catch(Exception e){
	    		System.out.println("Error ====" + e.getStackTrace());
	    		System.out.println("Error ====" + e.getMessage());
	    	}
		return null;
	}
}
