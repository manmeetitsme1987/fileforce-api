package fileforce.Controller;

import java.util.Map;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fileforce.FileForceApplication;
import fileforce.Configuration.BigOperation;
import fileforce.Model.Request.GoogleDriveRequest;
import fileforce.Model.Response.GoogleDriveFilesResponse;
import fileforce.Service.GoogleDriveService;

@RestController
public class GoogleDriveController {
	
	@Autowired private GoogleDriveService gDriveService;
	@Autowired private AmqpTemplate amqpTemplate;
	@Autowired private Queue rabbitQueue;
	
	@RequestMapping(value="/googleDriveData", method=RequestMethod.GET)
	public @ResponseBody GoogleDriveFilesResponse getGoogleDriveDataGet() {
        return this.gDriveService.getGoogleDriveData(null); 
    }
	
	@RequestMapping(value="/googleDriveData", method=RequestMethod.POST)
	public @ResponseBody GoogleDriveFilesResponse getGoogleDriveDataPost(@RequestBody GoogleDriveRequest gDriveRequest) {
        return this.gDriveService.getGoogleDriveData(gDriveRequest); 
    }
	
	@RequestMapping(value="/parseFiles", method=RequestMethod.POST)
	public @ResponseBody String parseFiles(@RequestBody GoogleDriveRequest gDriveRequest) {
        return this.gDriveService.parsefiles(gDriveRequest);  
    }
	
	
	/*
	 * RABBIT MQ Implementation
	 * 
	 */
	
	@ModelAttribute("bigOp")
    public BigOperation newBigOp() {
        return new BigOperation();
    }

    @RequestMapping()
    public String display() {
        return "bigOpSubmissionForm"; 
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String process(@ModelAttribute("bigOp") BigOperation bigOp, Map<String,Object> map) {
        // Receives the bigOp from the form submission, converts to a message, and sends to RabbitMQ.
        ApplicationContext context = new AnnotationConfigApplicationContext(FileForceApplication.class);
        AmqpTemplate amqpTemplate = context.getBean(AmqpTemplate.class);
        amqpTemplate.convertAndSend(bigOp);

        System.out.println("Sent to RabbitMQ: " + bigOp);

        // Send the bigOp back to the confirmation page for displaying details in view
        map.put("bigOp", bigOp);
        return "bigOpReceivedConfirmation";
    }
    
    @RequestMapping(value="/testRabbitMQ", method=RequestMethod.GET)
	public @ResponseBody String testRabbitMQ() {
    	// Receives the bigOp from the form submission, converts to a message, and sends to RabbitMQ.
    	String bigOp = "Test";
    	ApplicationContext context = new AnnotationConfigApplicationContext(FileForceApplication.class);
    	AmqpTemplate amqpTemplate = context.getBean(AmqpTemplate.class);
    	amqpTemplate.convertAndSend(rabbitQueue.getName(), bigOp);
    	
        System.out.println("Sent to RabbitMQ: " + bigOp);
        // Send the bigOp back to the confirmation page for displaying details in view
        return "bigOpReceivedConfirmation"; 
    }
}
