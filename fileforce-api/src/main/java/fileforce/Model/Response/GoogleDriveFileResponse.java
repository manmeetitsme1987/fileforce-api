package fileforce.Model.Response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class GoogleDriveFileResponse {
	private String kind;
	private String id;
	private String etag;
	private String selfLink;
	private String webContentLink;
	private String alternateLink;
	private String embedLink;
	private String iconLink;
	private String thumbnailLink;
	private String title;
	private String mimeType;
	private Labels labels;
	private String createdDate;
	private String modifiedDate;
	private String lastViewedByMeDate;
	private String markedViewedByMeDate;
	private String sharedWithMeDate;
	private String version;
	private ExportLinks exportLinks;
	private UserPermission userPermission;
	private String quotaBytesUsed;
	private List<String> ownerNames;
	private List<Owners> owners;
	private String lastModifyingUserName;
	private Owners lastModifyingUser;
	private Capabilities capabilities;
	private boolean editable;
	private boolean copyable;
	private boolean writersCanShare;
	private boolean shared;
	private boolean explicitlyTrashed;
	private boolean appDataContents;
	private List<String> spaces;
	private String downloadUrl;
	private String fileExtension;
	private String md5Checksum;
	private String fileSize;
	private String headRevisionId;
	private ImageMediaMetadata imageMediaMetadata;
	
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

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public String getSelfLink() {
		return selfLink;
	}

	public void setSelfLink(String selfLink) {
		this.selfLink = selfLink;
	}

	public String getWebContentLink() {
		return webContentLink;
	}

	public void setWebContentLink(String webContentLink) {
		this.webContentLink = webContentLink;
	}

	public String getAlternateLink() {
		return alternateLink;
	}

	public void setAlternateLink(String alternateLink) {
		this.alternateLink = alternateLink;
	}

	public String getEmbedLink() {
		return embedLink;
	}

	public void setEmbedLink(String embedLink) {
		this.embedLink = embedLink;
	}

	public String getIconLink() {
		return iconLink;
	}

	public void setIconLink(String iconLink) {
		this.iconLink = iconLink;
	}

	public String getThumbnailLink() {
		return thumbnailLink;
	}

	public void setThumbnailLink(String thumbnailLink) {
		this.thumbnailLink = thumbnailLink;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Labels getLabels() {
		return labels;
	}

	public void setLabels(Labels labels) {
		this.labels = labels;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getLastViewedByMeDate() {
		return lastViewedByMeDate;
	}

	public void setLastViewedByMeDate(String lastViewedByMeDate) {
		this.lastViewedByMeDate = lastViewedByMeDate;
	}

	public String getMarkedViewedByMeDate() {
		return markedViewedByMeDate;
	}

	public void setMarkedViewedByMeDate(String markedViewedByMeDate) {
		this.markedViewedByMeDate = markedViewedByMeDate;
	}

	public String getSharedWithMeDate() {
		return sharedWithMeDate;
	}

	public void setSharedWithMeDate(String sharedWithMeDate) {
		this.sharedWithMeDate = sharedWithMeDate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ExportLinks getExportLinks() {
		return exportLinks;
	}

	public void setExportLinks(ExportLinks exportLinks) {
		this.exportLinks = exportLinks;
	}

	public UserPermission getUserPermission() {
		return userPermission;
	}

	public void setUserPermission(UserPermission userPermission) {
		this.userPermission = userPermission;
	}

	public String getQuotaBytesUsed() {
		return quotaBytesUsed;
	}

	public void setQuotaBytesUsed(String quotaBytesUsed) {
		this.quotaBytesUsed = quotaBytesUsed;
	}

	public List<String> getOwnerNames() {
		return ownerNames;
	}

	public void setOwnerNames(List<String> ownerNames) {
		this.ownerNames = ownerNames;
	}

	public List<Owners> getOwners() {
		return owners;
	}

	public void setOwners(List<Owners> owners) {
		this.owners = owners;
	}

	public String getLastModifyingUserName() {
		return lastModifyingUserName;
	}

	public void setLastModifyingUserName(String lastModifyingUserName) {
		this.lastModifyingUserName = lastModifyingUserName;
	}

	public Owners getLastModifyingUser() {
		return lastModifyingUser;
	}

	public void setLastModifyingUser(Owners lastModifyingUser) {
		this.lastModifyingUser = lastModifyingUser;
	}

	public Capabilities getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(Capabilities capabilities) {
		this.capabilities = capabilities;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isCopyable() {
		return copyable;
	}

	public void setCopyable(boolean copyable) {
		this.copyable = copyable;
	}

	public boolean isWritersCanShare() {
		return writersCanShare;
	}

	public void setWritersCanShare(boolean writersCanShare) {
		this.writersCanShare = writersCanShare;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public boolean isExplicitlyTrashed() {
		return explicitlyTrashed;
	}

	public void setExplicitlyTrashed(boolean explicitlyTrashed) {
		this.explicitlyTrashed = explicitlyTrashed;
	}

	public boolean isAppDataContents() {
		return appDataContents;
	}

	public void setAppDataContents(boolean appDataContents) {
		this.appDataContents = appDataContents;
	}

	public List<String> getSpaces() {
		return spaces;
	}

	public void setSpaces(List<String> spaces) {
		this.spaces = spaces;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getMd5Checksum() {
		return md5Checksum;
	}

	public void setMd5Checksum(String md5Checksum) {
		this.md5Checksum = md5Checksum;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getHeadRevisionId() {
		return headRevisionId;
	}

	public void setHeadRevisionId(String headRevisionId) {
		this.headRevisionId = headRevisionId;
	}

	public ImageMediaMetadata getImageMediaMetadata() {
		return imageMediaMetadata;
	}

	public void setImageMediaMetadata(ImageMediaMetadata imageMediaMetadata) {
		this.imageMediaMetadata = imageMediaMetadata;
	}

	
	
	
	public class Labels{
		private boolean starred;
		private boolean hidden;
		private boolean trashed;
		private boolean restricted;
		private boolean viewed;
		
		public boolean isStarred() {
			return starred;
		}
		public void setStarred(boolean starred) {
			this.starred = starred;
		}
		public boolean isHidden() {
			return hidden;
		}
		public void setHidden(boolean hidden) {
			this.hidden = hidden;
		}
		public boolean isTrashed() {
			return trashed;
		}
		public void setTrashed(boolean trashed) {
			this.trashed = trashed;
		}
		public boolean isRestricted() {
			return restricted;
		}
		public void setRestricted(boolean restricted) {
			this.restricted = restricted;
		}
		public boolean isViewed() {
			return viewed;
		}
		public void setViewed(boolean viewed) {
			this.viewed = viewed;
		}
		
	}
	
	public class ExportLinks{
		private String applicationRTF;
		private String openDocumentTetxt;
		private String textHTML;
		private String applicationPDF;
		private String applicationEpubZip;
		private String applicationZIP;
		private String wordDocument;
		private String textPlain;
		
		@JsonProperty(value="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
		private String applicationSpreadSheet;
		
		public String getTextCSV() {
			return textCSV;
		}

		public String getApplicationSpreadSheet() {
			return applicationSpreadSheet;
		}

		public void setApplicationSpreadSheet(String applicationSpreadSheet) {
			this.applicationSpreadSheet = applicationSpreadSheet;
		}

		public void setTextCSV(String textCSV) {
			this.textCSV = textCSV;
		}

		private String textCSV;
		
		@JsonProperty(value="application/rtf")
		public String getApplicationRTF() {
			return applicationRTF;
		}

		@JsonProperty(value="application/rtf")
		public void setApplicationRTF(String applicationRTF) {
			this.applicationRTF = applicationRTF;
		}
		
		@JsonProperty(value="application/vnd.oasis.opendocument.text")
		public String getOpenDocumentTetxt() {
			return openDocumentTetxt;
		}
		
		@JsonProperty(value="application/vnd.oasis.opendocument.text")
		public void setOpenDocumentTetxt(String openDocumentTetxt) {
			this.openDocumentTetxt = openDocumentTetxt;
		}
		
		@JsonProperty(value="text/html")
		public String getTextHTML() {
			return textHTML;
		}
		
		@JsonProperty(value="text/html")
		public void setTextHTML(String textHTML) {
			this.textHTML = textHTML;
		}
		
		@JsonProperty(value="application/pdf")
		public String getApplicationPDF() {
			return applicationPDF;
		}
		
		@JsonProperty(value="application/pdf")
		public void setApplicationPDF(String applicationPDF) {
			this.applicationPDF = applicationPDF;
		}
		
		@JsonProperty(value="application/epub+zip")
		public String getApplicationEpubZip() {
			return applicationEpubZip;
		}
		
		@JsonProperty(value="application/epub+zip")
		public void setApplicationEpubZip(String applicationEpubZip) {
			this.applicationEpubZip = applicationEpubZip;
		}
		
		@JsonProperty(value="application/zip")
		public String getApplicationZIP() {
			return applicationZIP;
		}
		
		@JsonProperty(value="application/zip")
		public void setApplicationZIP(String applicationZIP) {
			this.applicationZIP = applicationZIP;
		}
		
		@JsonProperty(value="application/vnd.openxmlformats-officedocument.wordprocessingml.document")
		public String getWordDocument() {
			return wordDocument;
		}

		@JsonProperty(value="application/vnd.openxmlformats-officedocument.wordprocessingml.document")
		public void setWordDocument(String wordDocument) {
			this.wordDocument = wordDocument;
		}
		
		@JsonProperty(value="text/plain")
		public String getTextPlain() {
			return textPlain;
		}
		
		@JsonProperty(value="text/plain")
		public void setTextPlain(String textPlain) {
			this.textPlain = textPlain;
		}

	}
	
	public class UserPermission{
		private String kind;
		private String etag;
		private String id;
		private String selfLink;
		private String role;
		private String type;
		
		public String getKind() {
			return kind;
		}
		public void setKind(String kind) {
			this.kind = kind;
		}
		public String getEtag() {
			return etag;
		}
		public void setEtag(String etag) {
			this.etag = etag;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getSelfLink() {
			return selfLink;
		}
		public void setSelfLink(String selfLink) {
			this.selfLink = selfLink;
		}
		public String getRole() {
			return role;
		}
		public void setRole(String role) {
			this.role = role;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
	}
	
	public class Owners{
		private String kind;
		private String displayName;
		private Picture picture;
		private String permissionId;
		private String emailAddress;
		private boolean isAuthenticatedUser;
		
		public String getKind() {
			return kind;
		}
		public void setKind(String kind) {
			this.kind = kind;
		}
		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		public Picture getPicture() {
			return picture;
		}
		public void setPicture(Picture picture) {
			this.picture = picture;
		}
		public String getPermissionId() {
			return permissionId;
		}
		public void setPermissionId(String permissionId) {
			this.permissionId = permissionId;
		}
		public String getEmailAddress() {
			return emailAddress;
		}
		public void setEmailAddress(String emailAddress) {
			this.emailAddress = emailAddress;
		}
		public boolean isAuthenticatedUser() {
			return isAuthenticatedUser;
		}
		public void setAuthenticatedUser(boolean isAuthenticatedUser) {
			this.isAuthenticatedUser = isAuthenticatedUser;
		}
		
	}
	
	public class Capabilities{
		private boolean canCopy;
		private boolean canEdit;
		public boolean isCanCopy() {
			return canCopy;
		}
		public void setCanCopy(boolean canCopy) {
			this.canCopy = canCopy;
		}
		public boolean isCanEdit() {
			return canEdit;
		}
		public void setCanEdit(boolean canEdit) {
			this.canEdit = canEdit;
		}
	}
	
	public class Picture{
		private String url;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
	
	public class ImageMediaMetadata{
		private Integer width;
		private Integer height;
		private Integer rotation;
		private Location location;
		private String date;
		private String cameraMake;
		private String exposureTime;
		private String aperture;
		private boolean flashUsed;
		private Double focalLength;
		private Double isoSpeed;
		private String meteringMode;
		private String sensor;
		private String exposureMode;
		private String colorSpace;
		private String whiteBalance;
		private Double exposureBias;
		
		public Integer getWidth() {
			return width;
		}
		public void setWidth(Integer width) {
			this.width = width;
		}
		public Integer getHeight() {
			return height;
		}
		public void setHeight(Integer height) {
			this.height = height;
		}
		public Integer getRotation() {
			return rotation;
		}
		public void setRotation(Integer rotation) {
			this.rotation = rotation;
		}
		public Location getLocation() {
			return location;
		}
		public void setLocation(Location location) {
			this.location = location;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getCameraMake() {
			return cameraMake;
		}
		public void setCameraMake(String cameraMake) {
			this.cameraMake = cameraMake;
		}
		public String getExposureTime() {
			return exposureTime;
		}
		public void setExposureTime(String exposureTime) {
			this.exposureTime = exposureTime;
		}
		public String getAperture() {
			return aperture;
		}
		public void setAperture(String aperture) {
			this.aperture = aperture;
		}
		public boolean isFlashUsed() {
			return flashUsed;
		}
		public void setFlashUsed(boolean flashUsed) {
			this.flashUsed = flashUsed;
		}
		public Double getFocalLength() {
			return focalLength;
		}
		public void setFocalLength(Double focalLength) {
			this.focalLength = focalLength;
		}
		public Double getIsoSpeed() {
			return isoSpeed;
		}
		public void setIsoSpeed(Double isoSpeed) {
			this.isoSpeed = isoSpeed;
		}
		public String getMeteringMode() {
			return meteringMode;
		}
		public void setMeteringMode(String meteringMode) {
			this.meteringMode = meteringMode;
		}
		public String getSensor() {
			return sensor;
		}
		public void setSensor(String sensor) {
			this.sensor = sensor;
		}
		public String getExposureMode() {
			return exposureMode;
		}
		public void setExposureMode(String exposureMode) {
			this.exposureMode = exposureMode;
		}
		public String getColorSpace() {
			return colorSpace;
		}
		public void setColorSpace(String colorSpace) {
			this.colorSpace = colorSpace;
		}
		public String getWhiteBalance() {
			return whiteBalance;
		}
		public void setWhiteBalance(String whiteBalance) {
			this.whiteBalance = whiteBalance;
		}
		public Double getExposureBias() {
			return exposureBias;
		}
		public void setExposureBias(Double exposureBias) {
			this.exposureBias = exposureBias;
		}
		
	}
	
	public class Location{
		private Double latitude;
		private Double longitude;
		private Double altitude;
		
		public Double getLatitude() {
			return latitude;
		}
		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}
		public Double getLongitude() {
			return longitude;
		}
		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}
		public Double getAltitude() {
			return altitude;
		}
		public void setAltitude(Double altitude) {
			this.altitude = altitude;
		}
	}
}
