package fileforce.Mapper;

import java.util.List;

import fileforce.Model.Request.GoogleDriveRequestWorker;
import fileforce.Model.Response.MasterTableResponseWorker;

public interface CommonMapperWorker {

	//fetching master table data
	MasterTableResponseWorker getMasterData(String orgId);
	
	//fetch Google Drive Data
	List<GoogleDriveRequestWorker> getContentVersionData(String schemaName);
}
