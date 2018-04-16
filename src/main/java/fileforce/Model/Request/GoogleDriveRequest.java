package fileforce.Model.Request;

public class GoogleDriveRequest {
	private String refresh_Token;
	private String token_endpoint;
	private String clientId;
	private String clientSecret;
	private String clientRedirectURI;
	private String endpoint;
	
	public GoogleDriveRequest(String refresh_Token, String token_endpoint, String clientId, 
								String clientSecret, String clientRedirectURI, String endpoint){
		this.refresh_Token = refresh_Token;
		this.token_endpoint = token_endpoint;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.clientRedirectURI = clientRedirectURI;
		this.endpoint = endpoint;
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
}
