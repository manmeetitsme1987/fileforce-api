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
import fileforce.Model.Response.IndexServiceResponse;
import fileforce.Model.Response.MasterTableResponse;
import fileforce.Service.CommonService;



@RestController
public class CommonController {
	@Autowired
    private CommonService commonService;
	
	
	//This API service is for getting the Salesforce org information from the PG database. It expects Salesforce ORG id as URL Parameter
	@RequestMapping(value="/orgInfo/{orgId}", method=RequestMethod.GET)
	public @ResponseBody MasterTableResponse getMasterTableData(@PathVariable String orgId) {
        return this.commonService.getMasterTableDataByOrgId(orgId);
        
        /*
         * orgid = 00D1r000000TYde
         * Response  : {"orgId":"00D1r000000TYde","schemaName":"techspike"}
         */
    }
	
	
	//This API is used for creating the index for all GOOGLE DRIVE files. This is called from Salesforce batch which runs every hour and initially too.
	//In first call the initial_sync =  TRUE
	@RequestMapping(value="/indexjob/new", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<IndexServiceResponse> runIndexJob(@RequestBody CommonIndexRequest commonRequest) {
		IndexServiceResponse obj = commonService.runIndexRequest(commonRequest);
		return new ResponseEntity<IndexServiceResponse>(obj, HttpStatus.OK);
		
		/*
		 
		 SAMPLE JSON
		 
		{  
    "salesforce":{  
       "updateFilesURL":"https://ff-ts-dev-ed.my.salesforce.com/services/apexrest/MetafileService/updateMetafiles",
       "sessionId":"SESSION_ID_REMOVED",
       "filesLibraryId":null,
       "orgId":"00D1r000000TYde",
       "userName":"manmeetitsme1987+fileforce-techspike@gmail.com",
       "sourceOrg":"https://login.salesforce.com",
       "initial_sync":true
    },
    "platform":{  
       "refreshToken":"1/KuWjejdKhpbisf_cRRvJ-bUu35v_JgLiTxePAD4X1oI",
       "platformName":"GoogleDrive",
       "token_endpoint":"https://www.googleapis.com/oauth2/v4/token",
       "clientId":"619983446033-4d4i2ekkmfal2r29ngjegkc0t53qascs.apps.googleusercontent.com",
       "clientSecret":"zDJjNrgtxiVqOuy_C8pajVVm",
       "clientRedirectURI":"https://ff-ts-dev-ed.lightning.force.com/c/GoogleOAuthCompletion.app",
       "endpointAll":"https://www.googleapis.com/drive/v3/files",
       "endpointSingle":"https://www.googleapis.com/drive/v2/files",
       "platform_file_id":""
    }
 }
		 
		 *
		 *
		 */
    }
}
