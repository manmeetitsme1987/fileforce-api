package fileforce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fileforce.Model.Request.CommonIndexRequest;
import fileforce.Model.Response.IndexJobResponse;
import fileforce.Model.Response.MasterTableResponse;
import fileforce.Service.CommonService;



@RestController
public class CommonController {
	@Autowired
    private CommonService commonService;
	
	//352 - Retrieve a Base GET API service
	@RequestMapping(value="/indexJob/{orgId}", method=RequestMethod.GET)
	public @ResponseBody MasterTableResponse getMasterTableData(@PathVariable String orgId) {
        return this.commonService.getMasterTableDataByOrgId(orgId);
    }
	
	@RequestMapping(value="/indexjob/new", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<IndexJobResponse> runIndexJob(@RequestBody CommonIndexRequest commonRequest) {
		IndexJobResponse obj = commonService.runIndexRequest(commonRequest);
		return new ResponseEntity<IndexJobResponse>(obj, HttpStatus.OK);
    }
}
