package fileforce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fileforce.Model.Response.TestResponse;
import fileforce.Service.CommonService;



@RestController
public class CommonController {
	@Autowired
    private CommonService commonService;
	
	//352 - Retrieve a Base GET API service
	@RequestMapping(value="/user/{id}", method=RequestMethod.GET)
	public @ResponseBody TestResponse getUser(@PathVariable int id) {
        return this.commonService.getUserInfoById(id);
    }
}
