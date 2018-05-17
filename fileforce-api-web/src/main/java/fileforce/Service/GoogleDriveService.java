package fileforce.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.gson.Gson;

import fileforce.Helper.ParameterStringBuilder;
import fileforce.Helper.SelfParserUtility;
import fileforce.Model.Request.GoogleDriveRequest;
import fileforce.Model.Response.GoogleDriveAuthResponse;
import fileforce.Model.Response.GoogleDriveFileResponse;
import fileforce.Model.Response.GoogleDriveFilesResponse;
import fileforce.Model.Response.IndexServiceResponse;

@Service
public class GoogleDriveService {
	/** Application name. */
    private static final String APPLICATION_NAME =
        "Drive API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/drive-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
        	GoogleDriveService.class.getResourceAsStream("/client_secret_gdrive.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    //public static void main(String[] args) throws IOException {
        /*
    	// Build a new authorized API client service.
        Drive service = getDriveService();

        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list()
             .setPageSize(10)
             .setFields("nextPageToken, files(id, name)")
             .execute();
        List<File> files = result.getFiles();
        if (files == null || files.size() == 0) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
    	GoogleDriveRequest gDriveRequest = prepareDummyData();
    	GoogleDriveAuthResponse authResponse = getDriveDataWithRefreshToken(gDriveRequest);
    	if(authResponse != null){
    		fetchFiles(authResponse, gDriveRequest);
    	}
    	
    	//getIndividualFileData("1WdHzBAP7pwS0aahB-yOu0zU8ddfVXXDScjN_AIT6dBc");
    	//getTheFinalTextData();
    	//URL url = obj.getClass().getResource("/static/demodocx.docx");
    	//System.out.println(url.getPath());
    	//SelfParserUtility.readPDFFile("/Manmeet/Spring Boot/fileforce-api/src/main/resources/static/demopdf2.pdf");*/
    	
    	/*
    	GoogleDriveService obj = new GoogleDriveService();
    	GoogleDriveRequest gDriveRequest = new GoogleDriveRequest();
		gDriveRequest.setRefresh_Token("1/KuWjejdKhpbisf_cRRvJ-bUu35v_JgLiTxePAD4X1oI");
		gDriveRequest.setToken_endpoint("https://www.googleapis.com/oauth2/v4/token");
		gDriveRequest.setClientId("619983446033-4d4i2ekkmfal2r29ngjegkc0t53qascs.apps.googleusercontent.com");
		gDriveRequest.setClientSecret("zDJjNrgtxiVqOuy_C8pajVVm");
		gDriveRequest.setClientRedirectURI("https://ff-ts-dev-ed.lightning.force.com/c/GoogleOAuthCompletion.app");
		gDriveRequest.setEndpoint("https://www.googleapis.com/drive/v2/files");
		
		List<GoogleDriveRequest.GoogleDriveFileRequest> listFileObj = new ArrayList();
		GoogleDriveRequest.GoogleDriveFileRequest tempObj = gDriveRequest.new GoogleDriveFileRequest();
		//image
		tempObj.setPlatform_id("1rJb5YWXZp6NPtB-EUANwrHc5b94TjGfU");
		listFileObj.add(tempObj);
		
		tempObj = gDriveRequest.new GoogleDriveFileRequest();
		tempObj.setPlatform_id("1WdHzBAP7pwS0aahB-yOu0zU8ddfVXXDScjN_AIT6dBc");
		listFileObj.add(tempObj);
		
		tempObj = gDriveRequest.new GoogleDriveFileRequest();
		tempObj.setPlatform_id("1s5u-Sdq9-i-7h9OAP6fCZ2IybPSRL87n");
		listFileObj.add(tempObj);
		
		//xlsx
		tempObj = gDriveRequest.new GoogleDriveFileRequest();
		tempObj.setPlatform_id("1jTVywFwB2BoVSRgGeAoPKOpUyP_3F3K6K6NHOCFPdw8");
		listFileObj.add(tempObj);
		
		//pdf
		tempObj = gDriveRequest.new GoogleDriveFileRequest();
		tempObj.setPlatform_id("0B00Qjb6sW4_GUUl3eDJ0MkhuM0k");
		listFileObj.add(tempObj);
		
		gDriveRequest.setFiles(listFileObj);
		obj.parsefiles(gDriveRequest);
    	
    	*/
    	
    	//obj.getGoogleDriveData(null);
    //}
	
    public static GoogleDriveRequest prepareDummyData(){
    	GoogleDriveRequest gDriveRequest = new GoogleDriveRequest();
		gDriveRequest.setRefresh_Token("1/KuWjejdKhpbisf_cRRvJ-bUu35v_JgLiTxePAD4X1oI");
		gDriveRequest.setToken_endpoint("https://www.googleapis.com/oauth2/v4/token");
		gDriveRequest.setClientId("619983446033-4d4i2ekkmfal2r29ngjegkc0t53qascs.apps.googleusercontent.com");
		gDriveRequest.setClientSecret("zDJjNrgtxiVqOuy_C8pajVVm");
		gDriveRequest.setClientRedirectURI("https://ff-ts-dev-ed.lightning.force.com/c/GoogleOAuthCompletion.app");
		gDriveRequest.setEndpoint("https://www.googleapis.com/drive/v3/files");
		return gDriveRequest;
    }
    
	public GoogleDriveFilesResponse getGoogleDriveData(GoogleDriveRequest gDriveRequest){
		//if get request is being called for test purpose
		if(gDriveRequest == null){
			gDriveRequest = prepareDummyData();
		}
		GoogleDriveAuthResponse authResponse = getDriveDataWithRefreshToken(gDriveRequest);
		GoogleDriveFilesResponse gDriveResponseObj = null;
		return fetchFiles(authResponse, gDriveRequest, null, gDriveResponseObj);
	}
	
	public static GoogleDriveFilesResponse fetchFiles(GoogleDriveAuthResponse authResponse, 
									GoogleDriveRequest gDriveRequest, 
									String nextPageToken, 
									GoogleDriveFilesResponse gDriveResponseObj){
		
		String endpoint = gDriveRequest.getEndpoint() + "?pageSize=1000";
		HttpURLConnection connection = null;
		System.out.println(authResponse.getAccess_token());
		try{
			if(nextPageToken != null){
				endpoint += "&pageToken="+nextPageToken;
			}
			URL url = new URL(endpoint);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", "Bearer " + authResponse.getAccess_token());
			
		    //Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
		    String inputLine;
		    while ((inputLine = rd.readLine()) != null) {
		    	response.append(inputLine);
		    }
		    rd.close();
		    Gson gson = new Gson();
		    if(gDriveResponseObj == null){
		    	gDriveResponseObj = gson.fromJson(response.toString(), GoogleDriveFilesResponse.class);
		    }else{
		    	GoogleDriveFilesResponse tempObj = gson.fromJson(response.toString(), GoogleDriveFilesResponse.class);
		    	gDriveResponseObj.getFiles().addAll(tempObj.getFiles());
		    	gDriveResponseObj.setIncompleteSearch(tempObj.getIncompleteSearch());
		    	gDriveResponseObj.setNextPageToken(tempObj.getNextPageToken());
		    	gDriveResponseObj.setKind(tempObj.getKind());
		    }
		    if(gDriveResponseObj.getNextPageToken() != null){
		    	fetchFiles(authResponse, gDriveRequest, gDriveResponseObj.getNextPageToken(), gDriveResponseObj);
		    }
		    System.out.println(gDriveResponseObj.getFiles().size());		    
		    return gDriveResponseObj;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
		    if (connection != null) {
		      connection.disconnect();
		    }
		}
	}
	
	public static GoogleDriveAuthResponse getDriveDataWithRefreshToken(GoogleDriveRequest gDriveRequest){
		HttpURLConnection connection = null;
		try {
		    //Create connection
			String endPointURL = gDriveRequest.getToken_endpoint() + "?" +
				      "refresh_token=" + gDriveRequest.getRefresh_Token() + "&" +
					  "client_id=" + gDriveRequest.getClientId() + "&" +
				      "client_secret=" + gDriveRequest.getClientSecret() + "&" +
				      "redirect_uri=" + gDriveRequest.getClientRedirectURI() + "&" +
				      "grant_type=" + "refresh_token";
		    URL url = new URL(endPointURL);
		    connection = (HttpURLConnection) url.openConnection();
		    connection.setRequestMethod("POST");
		    connection.setRequestProperty("Content-Length", "0");
		    connection.setUseCaches(false);
		    connection.setDoOutput(true);
		    
		    //connection.setRequestProperty("Content-Type", 
		    //    "application/x-www-form-urlencoded");

		    //connection.setRequestProperty("Content-Language", "en-US");  
		    Map<String, String> parameters = new HashMap<>();
		    //Send request
		    DataOutputStream wr = new DataOutputStream (
		        connection.getOutputStream());
		    wr.writeBytes(ParameterStringBuilder.getParamsString(parameters));
		    wr.close();

		    //Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
		    String line;
		    while ((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    Gson gson = new Gson();
		    GoogleDriveAuthResponse gDriveResponseObj = gson.fromJson(response.toString(), GoogleDriveAuthResponse.class);
		    //System.out.println(gDriveResponseObj.getAccess_token() + "=== Access Token"); 
		    return gDriveResponseObj;
		  } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		  } finally {
		    if (connection != null) {
		      connection.disconnect();
		    }
		  }
	}
	
	public String parsefiles(GoogleDriveRequest gDriveRequest){
		if(gDriveRequest == null){
			gDriveRequest = prepareDummyData();
		}
		GoogleDriveAuthResponse gDriveResponseObj = getDriveDataWithRefreshToken(gDriveRequest);
		Map<String, IndexServiceResponse> mapPlatformIdBody = new HashMap();
		if(gDriveResponseObj.getAccess_token() != null){
			for(GoogleDriveRequest.GoogleDriveFileRequest fileRequest : gDriveRequest.getFiles()){
				getIndividualFileData(fileRequest, gDriveRequest, gDriveResponseObj, mapPlatformIdBody);
			}
		}
		//System.out.println(mapPlatformIdBody.keySet().toString() + "=== Size of Map");
		Gson gson = new Gson(); 
		String json = gson.toJson(mapPlatformIdBody);
		System.out.println(json + "=== converted JSON");
		//System.out.println(mapPlatformIdBody.get("1jTVywFwB2BoVSRgGeAoPKOpUyP_3F3K6K6NHOCFPdw8"));
		return null;
	}
	
	public static void getIndividualFileData(GoogleDriveRequest.GoogleDriveFileRequest fileRequest, 
												GoogleDriveRequest gDriveRequest,
												GoogleDriveAuthResponse gDriveResponseObj,
												Map<String, IndexServiceResponse> mapPlatformIdBody){
		HttpURLConnection connection = null;
		try{
			URL url = new URL(gDriveRequest.getEndpoint() + "/" + fileRequest.getPlatform_id());
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", "Bearer " + gDriveResponseObj.getAccess_token());
			
		    //Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
		    String inputLine;
		    while ((inputLine = rd.readLine()) != null) {
		    	if(inputLine.contains("text/plain")){
		    		inputLine = inputLine.replaceAll("text/plain", "textPlain");
		    	}
		    	if(inputLine.contains("text/csv")){
		    		inputLine = inputLine.replaceAll("text/csv", "textCSV");
		    	}
		    	
		    	if(inputLine.contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")){
		    		inputLine = inputLine.replaceAll("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "applicationSpreadSheet");
		    	}
		    	
		    	if(inputLine.contains("application/pdf")){
		    		inputLine = inputLine.replaceAll("application/pdf", "applicationPDF");
		    	}
		    	
		    	response.append(inputLine);
		    	response.append("\r");
		    }
		    rd.close();
		    Gson gson = new Gson();
		    GoogleDriveFileResponse gDriveFileResponseObj = gson.fromJson(response.toString(), GoogleDriveFileResponse.class);
		    mapPlatformIdBody.put(gDriveFileResponseObj.getId(), new IndexServiceResponse(gDriveFileResponseObj.getTitle(),
																							gDriveFileResponseObj.getKind(),
																							gDriveFileResponseObj.getMimeType(),
																							CommonService.GOOGLE_DRIVE,
																							gDriveFileResponseObj.getId(),
																							gDriveFileResponseObj.getWebContentLink(),
																							""));
		    //not supporting Images for now
		    String fileTitle = gDriveFileResponseObj.getTitle().toLowerCase();
		    if(!(fileTitle.contains("jpeg") || fileTitle.contains("jpg"))){
		    	if(fileTitle.contains("doc") || fileTitle.contains("docx")){
			    	if(gDriveFileResponseObj.getExportLinks() != null && gDriveFileResponseObj.getExportLinks().getTextPlain() != null){
				    	getTheFinalTextData(gDriveFileResponseObj, mapPlatformIdBody);
				    }
		    	}else if(gDriveFileResponseObj.getMimeType().contains("spreadsheet")){
		    		SelfParserUtility.readXLSXFile(gDriveFileResponseObj.getExportLinks().getApplicationSpreadSheet(), 
													gDriveFileResponseObj.getId(), 
													mapPlatformIdBody);
		    		
		    	}else if(gDriveFileResponseObj.getMimeType().contains("applicationPDF")){
		    		SelfParserUtility.readPDFFile(gDriveFileResponseObj.getWebContentLink(), 
							gDriveFileResponseObj.getId(), 
							mapPlatformIdBody);
		    	}
		    }
		    //System.out.println(response.toString());		    
		    //return response.toString();
		}catch(Exception e){
			e.printStackTrace();
			//return null;
		}finally{
		    if (connection != null) {
		      connection.disconnect();
		    }
		}
	}
	
	
	public static void getTheFinalTextData(GoogleDriveFileResponse gDriveFileResponseObj, Map<String, IndexServiceResponse> mapPlatformIdBody){
		//String endpoint = "https://docs.google.com/feeds/download/documents/export/Export?id=1WdHzBAP7pwS0aahB-yOu0zU8ddfVXXDScjN_AIT6dBc&exportFormat=txt";
		HttpURLConnection connection = null;
		try{
			URL url = new URL(gDriveFileResponseObj.getExportLinks().getTextPlain());
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
		    //Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
		    String inputLine;
		    while ((inputLine = rd.readLine()) != null) {
		    	response.append(inputLine);
		    	//response.append("\n");
		    }
		    rd.close();
		    mapPlatformIdBody.get(gDriveFileResponseObj.getId()).setIndex(response.toString());
		}catch(Exception e){
			e.printStackTrace();
			mapPlatformIdBody.get(gDriveFileResponseObj.getId()).setErrorMessage(e.getMessage());
			//return null;
		}finally{
		    if (connection != null) {
		      connection.disconnect();
		    }
		}
	}
}
