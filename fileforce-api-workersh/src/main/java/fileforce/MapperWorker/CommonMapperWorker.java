package fileforce.MapperWorker;


import java.util.List;

import org.apache.ibatis.annotations.Select;

import fileforce.Model.Request.GoogleDriveRequestWorker;
import fileforce.Model.Response.MasterTableResponseWorker;

public interface CommonMapperWorker {

	//fetching master table data
	@Select("select org_id as orgId, schema_val as schemaName from master_table WHERE org_id=#{orgId}")
	MasterTableResponseWorker getMasterData(String orgId);
	
	//fetch Google Drive Data
	@Select("select id as id, External_Id__c as externalId, FileExtension as fileExtension, "
			+ "FileType as fileType, FirstPublishLocationId as firstPublicationId, Title as title, "
			+ "ContentDocumentId as contentDocumentId from techspike.ContentVersion WHERE id is not null limit 20")
	List<GoogleDriveRequestWorker> getContentVersionData(String schemaName);
}
