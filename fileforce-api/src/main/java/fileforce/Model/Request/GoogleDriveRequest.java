package fileforce.Model.Request;

import java.util.List;

public class GoogleDriveRequest {
	private String refresh_Token;
	private String token_endpoint;
	private String clientId;
	private String clientSecret;
	private String clientRedirectURI;
	private String endpoint;
	private List<GoogleDriveFileRequest> files;
	
	public List<GoogleDriveFileRequest> getFiles() {
		return files;
	}
	public void setFiles(List<GoogleDriveFileRequest> files) {
		this.files = files;
	}
	public String getRefresh_Token() {
		return refresh_Token;
	}
	public void setRefresh_Token(String refresh_Token) {
		this.refresh_Token = refresh_Token;
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
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public class GoogleDriveFileRequest{
		private String mimeType;
		private String platform_id;
		
		public String getMimeType() {
			return mimeType;
		}
		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}
		public String getPlatform_id() {
			return platform_id;
		}
		public void setPlatform_id(String platform_id) {
			this.platform_id = platform_id;
		}
		
	}
}
