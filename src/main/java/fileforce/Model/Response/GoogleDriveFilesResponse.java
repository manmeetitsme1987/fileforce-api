package fileforce.Model.Response;

import java.util.List;

public class GoogleDriveFilesResponse {
	private String kind;
	private Boolean incompleteSearch;
    private List<GoogleFile> files;
    
    
    public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public Boolean getIncompleteSearch() {
		return incompleteSearch;
	}

	public void setIncompleteSearch(Boolean incompleteSearch) {
		this.incompleteSearch = incompleteSearch;
	}

	public List<GoogleFile> getFiles() {
		return files;
	}

	public void setFiles(List<GoogleFile> files) {
		this.files = files;
	}

	public class GoogleFile {
	    private String kind;
	    private String id;
	    private String name;
	    private String mimeType;
	    
		public String getKind() {
			return kind;
		}
		public void setKind(String kind) {
			this.kind = kind;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getMimeType() {
			return mimeType;
		}
		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}
	  }
}
