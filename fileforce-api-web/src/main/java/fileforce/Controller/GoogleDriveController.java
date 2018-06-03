package fileforce.Controller;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fileforce.Configuration.RabbitConfiguration;
import fileforce.Model.Request.GoogleDriveRequest;
import fileforce.Model.Response.GoogleDriveFilesResponse;
import fileforce.Service.GoogleDriveService;

@RestController
public class GoogleDriveController {
	
	@Autowired private GoogleDriveService gDriveService;
	@Autowired private Queue rabbitQueue;
	
	
	//This service is used to test the Google Drive authentication and working
	@RequestMapping(value="/googleDriveData", method=RequestMethod.GET)
	public @ResponseBody GoogleDriveFilesResponse getGoogleDriveDataGet() {
        return this.gDriveService.getGoogleDriveData(null); 
        /*
         {"kind":"drive#fileList","incompleteSearch":false,"files":
         	[{"kind":"drive#file","id":"1jTVywFwB2BoVSRgGeAoPKOpUyP_3F3K6K6NHOCFPdw8","name":"MPL2012","mimeType":"application/vnd.google-apps.spreadsheet"},
         	{"kind":"drive#file","id":"1G0KCrV403YbZ-WFkCGBEtEhw7Ep3o8buZfboRPSWzZw","name":"YTI–NE3-4N & 5D(2N Gangtok,2N Darjeeling)","mimeType":"application/vnd.google-apps.presentation"},
         	{"kind":"drive#file","id":"1FDAYihy6ih-qRD7zSC0q-vrnzSXnRiqBapUm6qivlqs","name":"Help Article for Talent Rover — Quick Apply","mimeType":"application/vnd.google-apps.document"},
         	{"kind":"drive#file","id":"0B_sPbI34vesKRklrVm5WS25tcXM","name":"enter otp.psd","mimeType":"image/x-photoshop"},
         	{"kind":"drive#file","id":"0B_sPbI34vesKQ2VZYXBlZDBPUG8","name":"splash.jpg","mimeType":"image/jpeg"}]}
         	
         	static/googleDriveData Response.json
         */
    }
	
	
	//This service is called from Salesforce when User Authorizes the google drive account. It creates the initial index and send it back to salesforce in 
	//synchronous way. 
	//OUTDATED
	@RequestMapping(value="/googleDriveData", method=RequestMethod.POST)
	public @ResponseBody GoogleDriveFilesResponse getGoogleDriveDataPost(@RequestBody GoogleDriveRequest gDriveRequest) {
        return this.gDriveService.getGoogleDriveData(gDriveRequest); 
    }
	
	
	//This service is used for parsing files directly. It could be used to see the response if parsing is failing for any file. 
	@RequestMapping(value="/parseFiles", method=RequestMethod.POST)
	public @ResponseBody String parseFiles(@RequestBody GoogleDriveRequest gDriveRequest) {
        return this.gDriveService.parsefiles(gDriveRequest);  
    }
    
	
	//This is test service to test the RabbitMQ functionality working
    @RequestMapping(value="/testRabbitMQ", method=RequestMethod.GET)
	public @ResponseBody String testRabbitMQ() {
    	// Receives the bigOp from the form submission, converts to a message, and sends to RabbitMQ.
    	try{
    	String bigOp = "Test";
    	ApplicationContext context = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
    	AmqpTemplate amqpTemplate = context.getBean(AmqpTemplate.class);
    	System.out.println(amqpTemplate + "====" + rabbitQueue.getName());
    	amqpTemplate.convertAndSend(rabbitQueue.getName(), bigOp);
    	
        System.out.println("Sent to RabbitMQ: " + bigOp);
        // Send the bigOp back to the confirmation page for displaying details in view
    	}catch(Exception e){
    		System.out.println("Error ====" + e.getMessage());
    	}
        return "bigOpReceivedConfirmation"; 
    }
}
