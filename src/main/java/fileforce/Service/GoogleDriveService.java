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
import fileforce.Model.Request.GoogleDriveRequest;
import fileforce.Model.Response.GoogleDriveAuthResponse;
import fileforce.Model.Response.GoogleDriveFilesResponse;

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

    /*public static void main(String[] args) throws IOException {
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
    }*/
	
    public static GoogleDriveRequest prepareDummyData(){
    	GoogleDriveRequest gDriveRequest = new GoogleDriveRequest();
		gDriveRequest.setRefresh_Token("1/rRLjhskgc93LO8FUivaMK-oY8P7pOe16wWS9EKy0pyih3kYil-qmONWxlWWhwqza");
		gDriveRequest.setToken_endpoint("https://www.googleapis.com/oauth2/v4/token");
		gDriveRequest.setClientId("619983446033-4d4i2ekkmfal2r29ngjegkc0t53qascs.apps.googleusercontent.com");
		gDriveRequest.setClientSecret("zDJjNrgtxiVqOuy_C8pajVVm");
		gDriveRequest.setClientRedirectURI("https://ff-ts-dev-ed.lightning.force.com/c/GoogleOAuthCompletion.app");
		gDriveRequest.setEndpoint("https://www.googleapis.com/drive/v3/files");
		return gDriveRequest;
    }
    
	public String getGoogleDriveData(GoogleDriveRequest gDriveRequest){
		//if get request is being called for test purpose
		if(gDriveRequest == null){
			gDriveRequest = prepareDummyData();
		}
		GoogleDriveAuthResponse authResponse = getDriveDataWithRefreshToken(gDriveRequest);
		return fetchFiles(authResponse, gDriveRequest);
	}
	
	public static String fetchFiles(GoogleDriveAuthResponse authResponse, GoogleDriveRequest gDriveRequest){
		String endpoint = gDriveRequest.getEndpoint();
		HttpURLConnection connection = null;
		System.out.println(authResponse.getAccess_token());
		try{
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
		    System.out.println(response.toString());		    
		    return response.toString();
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
}
