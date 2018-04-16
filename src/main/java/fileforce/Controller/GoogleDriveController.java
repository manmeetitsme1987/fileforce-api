package fileforce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fileforce.Model.Request.GoogleDriveRequest;
import fileforce.Service.GoogleDriveService;

@RestController
public class GoogleDriveController {
	
	@Autowired
    private GoogleDriveService gDriveService;
	
	@RequestMapping(value="/googleDriveData", method=RequestMethod.GET)
	public @ResponseBody String getGoogleDriveDataGet() {
        return this.gDriveService.getGoogleDriveData(null); 
    }
	
	@RequestMapping(value="/googleDriveData", method=RequestMethod.POST)
	public @ResponseBody String getGoogleDriveDataPost(@RequestBody GoogleDriveRequest gDriveRequest) {
        return this.gDriveService.getGoogleDriveData(gDriveRequest); 
    }
}
