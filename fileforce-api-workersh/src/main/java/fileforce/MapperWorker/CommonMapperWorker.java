package fileforce.MapperWorker;


import java.util.List;

import org.apache.ibatis.annotations.Select;

import fileforce.Model.Request.ContentVersionRequest;
import fileforce.Model.Response.ContentVersionResponseWorker;
import fileforce.Model.Response.MasterTableResponseWorker;

public interface CommonMapperWorker {

	//fetching master table data
	@Select("select org_id as orgId, schema_val as schemaName from master_table WHERE org_id=#{orgId}")
	MasterTableResponseWorker getMasterDataWorker(String orgId);
	
	//fetch Google Drive Data
	@Select("select id as id, External_Id__c as externalId, FileExtension as fileExtension, "
			+ "FileType as fileType, FirstPublishLocationId as firstPublicationId, Title as title, "
			+ "ContentDocumentId as contentDocumentId from ${schemaName}.ContentVersion WHERE id is not null limit ${limit}")
	List<ContentVersionResponseWorker> getContentVersionData(ContentVersionRequest contentVersionRequest);
}
