package fileforce.Model.Response;

public class IndexJobResponse {
	private String jobId;
	private String jobStatus;
	private String errorStatus;
	private String errorMessage;
	private String indexBodyResponse;
	
	
	public String getIndexBodyResponse() {
		return indexBodyResponse;
	}
	public void setIndexBodyResponse(String indexBodyResponse) {
		this.indexBodyResponse = indexBodyResponse;
	}
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
	public String getErrorStatus() {
		return errorStatus;
	}
	public void setErrorStatus(String errorStatus) {
		this.errorStatus = errorStatus;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}


/*
 SAMPLE Response
 This is the expected response body for a good (200 OK) response:

{
    "jobId":"some_job_id"
    "jobStatus":"processing"
}
This is the expected response body for a bad response:

{
    "errorStatus":"some_status_code"
    "errorMessage":"error_message_describing_problem"
}
 
 */
