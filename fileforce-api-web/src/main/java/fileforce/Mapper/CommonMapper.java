package fileforce.Mapper;


import fileforce.Model.Response.ContentVersionResponse;
import fileforce.Model.Response.MasterTableResponse;

public interface CommonMapper {
	
	    //fetching master table data
		MasterTableResponse getMasterData(String orgId);
		
		/*
		@Select("select id as id, External_Id__c as externalId, FileExtension as fileExtension, "
				+ "FileType as fileType, FirstPublishLocationId as firstPublicationId, Title as title, "
				+ "ContentDocumentId as contentDocumentId from techspike.ContentVersion WHERE id is not null AND External_Id__c = #{platformFileId}")
		*/
		//fetch Google Drive Data
		ContentVersionResponse getContentVersionData(String platformFileId);
		
}
