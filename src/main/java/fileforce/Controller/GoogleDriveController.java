package fileforce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fileforce.Model.Request.GoogleDriveRequest;
import fileforce.Model.Response.GoogleDriveFilesResponse;
import fileforce.Service.GoogleDriveService;

@RestController
public class GoogleDriveController {
	
	@Autowired
    private GoogleDriveService gDriveService;
	
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
}
