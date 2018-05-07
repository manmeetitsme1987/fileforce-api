package fileforce.Model.Response;

public class IndexServiceResponse {
	
	private String errorMessage;
	private String platform_id;
	private String indexedBody;
	
	
	public String getPlatform_id() {
		return platform_id;
	}
	public void setPlatform_id(String platform_id) {
		this.platform_id = platform_id;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getIndexedBody() {
		return indexedBody;
	}
	public void setIndexedBody(String indexedBody) {
		this.indexedBody = indexedBody;
	}
	
	
	public IndexServiceResponse(String errorMessage, String indexedBody){
		this.errorMessage = errorMessage;
		this.indexedBody = indexedBody;
	}
	
	public IndexServiceResponse(String platform_id, String errorMessage, String indexedBody){
		this.platform_id = platform_id;
		this.errorMessage = errorMessage;
		this.indexedBody = indexedBody;
	}
}
