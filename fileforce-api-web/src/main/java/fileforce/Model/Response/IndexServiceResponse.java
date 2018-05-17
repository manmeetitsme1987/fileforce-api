package fileforce.Model.Response;

public class IndexServiceResponse {
	
	private String errorMessage;
	private String index;
	private String platform_id;
	private String type	;						
	private String mimeType;						
	private String platform	;					
	private String url			;				
	private String firstPublishLocationId;
	private String name;
	private Job jobInfo;
	
	public Job getJobInfo() {
		return jobInfo;
	}
	public void setJobInfo(Job jobInfo) {
		this.jobInfo = jobInfo;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFirstPublishLocationId() {
		return firstPublishLocationId;
	}
	public void setFirstPublishLocationId(String firstPublishLocationId) {
		this.firstPublishLocationId = firstPublishLocationId;
	}
	
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
	
	public IndexServiceResponse(String name, String type, String mimeType, String platform, String platform_id, String url, String firstPublishLocationId){
		this.name = name;
		this.type = type;
		this.mimeType = mimeType;
		this.platform = platform;
		this.platform_id = platform_id;
		this.url = url;
		this.firstPublishLocationId = firstPublishLocationId;
		
	}
	
	public IndexServiceResponse(){
		
	}
	
	public class Job{
		private String jobId;
		private String jobStatus;
		private String message;
		
		public String getJobId() {
			return jobId;
		}
		public void setJobId(String jobId) {
			this.jobId = jobId;
		}
		public String getJobStatus() {
			return jobStatus;
		}
		public void setJobStatus(String jobStatus) {
			this.jobStatus = jobStatus;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
	}
}
