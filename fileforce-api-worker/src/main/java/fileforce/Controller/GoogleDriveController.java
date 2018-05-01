package fileforce.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleDriveController {
	
	@RequestMapping(value="/googleDriveDataWorker", method=RequestMethod.GET)
	public @ResponseBody String getGoogleDriveDataGet() {
        return "Served from workder dyno"; 
    }
}
