package fileforce.Model.Request;

public class ContentVersionRequest {
	private String schemaName;
	private Integer limit;
	
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	
	public ContentVersionRequest(String schemaName, Integer limit){
		this.schemaName = schemaName;
		if(limit != null){
			this.limit = limit;
		}
	}
	
}
