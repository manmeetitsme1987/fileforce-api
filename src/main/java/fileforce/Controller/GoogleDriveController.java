package fileforce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fileforce.Model.Response.GoogleDriveFilesResponse;
import fileforce.Service.GoogleDriveService;

@RestController
public class GoogleDriveController {
	
	@Autowired
    private GoogleDriveService gDriveService;
	
	@RequestMapping(value="/googleDriveData", method=RequestMethod.GET)
	public @ResponseBody GoogleDriveFilesResponse getGoogleDriveData() {
        return this.gDriveService.getGoogleDriveData(); 
    }
}
