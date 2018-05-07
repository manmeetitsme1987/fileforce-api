package fileforce.Model.Request;

public class CommonIndexRequest {
	
	private SalesforceClass salesforce;
	private PlatformClass platform;
	
	public SalesforceClass getSalesforce() {
		return salesforce;
	}

	public void setSalesforce(SalesforceClass salesforce) {
		this.salesforce = salesforce;
	}

	public PlatformClass getPlatform() {
		return platform;
	}

	public void setPlatform(PlatformClass platform) {
		this.platform = platform;
	}

	public class SalesforceClass{
		private String updateFilesURL;
		private String sessionId;
		private String filesLibraryId;
		private String orgId;
		
		public String getOrgId() {
			return orgId;
		}
		public void setOrgId(String orgId) {
			this.orgId = orgId;
		}
		public String getUpdateFilesURL() {
			return updateFilesURL;
		}
		public void setUpdateFilesURL(String updateFilesURL) {
			this.updateFilesURL = updateFilesURL;
		}
		public String getSessionId() {
			return sessionId;
		}
		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}
		public String getFilesLibraryId() {
			return filesLibraryId;
		}
		public void setFilesLibraryId(String filesLibraryId) {
			this.filesLibraryId = filesLibraryId;
		}
		
	}
	
	public class PlatformClass{
		private String refreshToken;
		private String platformName;
		private String token_endpoint;
		private String clientId;
		private String clientSecret;
		private String clientRedirectURI;
		private String endpoint;
		private String platform_file_id;
		
		public String getPlatform_file_id() {
			return platform_file_id;
		}
		public void setPlatform_file_id(String platform_file_id) {
			this.platform_file_id = platform_file_id;
		}
		public String getEndpoint() {
			return endpoint;
		}
		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}
		public String getToken_endpoint() {
			return token_endpoint;
		}
		public void setToken_endpoint(String token_endpoint) {
			this.token_endpoint = token_endpoint;
		}
		public String getClientId() {
			return clientId;
		}
		public void setClientId(String clientId) {
			this.clientId = clientId;
		}
		public String getClientSecret() {
			return clientSecret;
		}
		public void setClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
		}
		public String getClientRedirectURI() {
			return clientRedirectURI;
		}
		public void setClientRedirectURI(String clientRedirectURI) {
			this.clientRedirectURI = clientRedirectURI;
		}
		
		public String getRefreshToken() {
			return refreshToken;
		}
		public void setRefreshToken(String refreshToken) {
			this.refreshToken = refreshToken;
		}
		public String getPlatformName() {
			return platformName;
		}
		public void setPlatformName(String platformName) {
			this.platformName = platformName;
		}
		
	}
}

/*
 
 Sample Request
 
  {  
    "salesforce":{  
       "updateFilesURL":"https://ff-ts-dev-ed.my.salesforce.com/services/apexrest/MetafileService/updateMetafiles",
       "sessionId":"SESSION_ID_REMOVED",
       "filesLibraryId":null,
       "orgId":"00D1r000000TYde"
    },
    "platform":{  
       "refreshToken":"1/KuWjejdKhpbisf_cRRvJ-bUu35v_JgLiTxePAD4X1oI",
       "platformName":"GoogleDrive",
       "token_endpoint":"https://www.googleapis.com/oauth2/v4/token",
       "clientId":"619983446033-4d4i2ekkmfal2r29ngjegkc0t53qascs.apps.googleusercontent.com",
       "clientSecret":"zDJjNrgtxiVqOuy_C8pajVVm",
       "clientRedirectURI":"https://ff-ts-dev-ed.lightning.force.com/c/GoogleOAuthCompletion.app",
       "endpoint":"https://www.googleapis.com/drive/v2/files"
    }
 }
 * */
