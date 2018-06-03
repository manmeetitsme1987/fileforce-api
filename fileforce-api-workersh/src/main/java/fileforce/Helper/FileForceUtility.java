package fileforce.Helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import fileforce.Model.Request.CommonIndexRequest;
import fileforce.Model.Response.GoogleDriveAuthResponseWorker;
import fileforce.Model.Response.GoogleDriveFilesResponseWorker;

public class FileForceUtility {
	
	//method to fetch all the google drive files
	public static GoogleDriveFilesResponseWorker fetchAllGoogleDriveFiles(CommonIndexRequest commonRequest){
		GoogleDriveAuthResponseWorker authResponse = getDriveDataWithRefreshToken(commonRequest);
		GoogleDriveFilesResponseWorker gDriveResponseObj = null;
		return fetchFiles(authResponse, commonRequest, null, gDriveResponseObj);
	}
	
	public static GoogleDriveAuthResponseWorker getDriveDataWithRefreshToken(CommonIndexRequest commonRequest){
		HttpURLConnection connection = null;
		try {
		    //Create connection
			String endPointURL = commonRequest.getPlatform().getToken_endpoint() + "?" +
				      "refresh_token=" + commonRequest.getPlatform().getRefreshToken() + "&" +
					  "client_id=" + commonRequest.getPlatform().getClientId() + "&" +
				      "client_secret=" + commonRequest.getPlatform().getClientSecret() + "&" +
				      "redirect_uri=" + commonRequest.getPlatform().getClientRedirectURI() + "&" +
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
		    GoogleDriveAuthResponseWorker gDriveResponseObj = gson.fromJson(response.toString(), GoogleDriveAuthResponseWorker.class);
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
	
	public static GoogleDriveFilesResponseWorker fetchFiles(GoogleDriveAuthResponseWorker authResponse, 
			CommonIndexRequest commonRequest, 
			String nextPageToken, 
			GoogleDriveFilesResponseWorker gDriveResponseObj){

			String endpoint = commonRequest.getPlatform().getEndpointAll() + "?pageSize=1000";
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
				gDriveResponseObj = gson.fromJson(response.toString(), GoogleDriveFilesResponseWorker.class);
			}else{
				GoogleDriveFilesResponseWorker tempObj = gson.fromJson(response.toString(), GoogleDriveFilesResponseWorker.class);
				gDriveResponseObj.getFiles().addAll(tempObj.getFiles());
				gDriveResponseObj.setIncompleteSearch(tempObj.getIncompleteSearch());
				gDriveResponseObj.setNextPageToken(tempObj.getNextPageToken());
				gDriveResponseObj.setKind(tempObj.getKind());
			}
			if(gDriveResponseObj.getNextPageToken() != null){
				fetchFiles(authResponse, commonRequest, gDriveResponseObj.getNextPageToken(), gDriveResponseObj);
			}
			return gDriveResponseObj;
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("===In Exception===" + e.getMessage());
			return null;
			}finally{
				if (connection != null) {
					connection.disconnect();
				}
			}
		}
}
