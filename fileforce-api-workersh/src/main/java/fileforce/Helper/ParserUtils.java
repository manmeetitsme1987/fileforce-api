package fileforce.Helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

import fileforce.Controller.AsyncProcessWorker;
import fileforce.Model.Request.CommonIndexRequest;
import fileforce.Model.Response.ContentVersionResponseWorker;
import fileforce.Model.Response.GoogleDriveAuthResponseWorker;
import fileforce.Model.Response.GoogleDriveFileResponseWorker;
import fileforce.Model.Response.IndexServiceResponseWorker;

public class ParserUtils {
	
	public static Map<String, IndexServiceResponseWorker> parsefiles(CommonIndexRequest commonRequest, List<ContentVersionResponseWorker> gDriveFileList){
		GoogleDriveAuthResponseWorker gDriveResponseObj = getDriveDataWithRefreshToken(commonRequest);
		Map<String, IndexServiceResponseWorker> mapPlatformIdBody = new HashMap();
		if(gDriveResponseObj.getAccess_token() != null){
			Integer countRecords = 1;
			for(ContentVersionResponseWorker fileRequest : gDriveFileList){
				if(!fileRequest.getExternalId().isEmpty()){
					System.out.println("Processing =========" + countRecords + "=== file Name ====" + fileRequest.getTitle());
					getIndividualFileData(fileRequest, commonRequest, gDriveResponseObj, mapPlatformIdBody);
					countRecords++;
					//break;
				}
			}
		}
		Gson gson = new Gson(); 
		String json = gson.toJson(mapPlatformIdBody);
		//System.out.println("converted JSON=========" + json + "=== converted JSON");
		return mapPlatformIdBody;
	}
	
	//method to get the access token
	private static GoogleDriveAuthResponseWorker getDriveDataWithRefreshToken(CommonIndexRequest commonRequest){
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
	
	
	public static void getIndividualFileData(ContentVersionResponseWorker fileRequest, 
												CommonIndexRequest commonIndexRequest,
												GoogleDriveAuthResponseWorker gDriveResponseObj,
												Map<String, IndexServiceResponseWorker> mapPlatformIdBody){
				HttpURLConnection connection = null;
				Set<String> tokens = new HashSet<String>();
				try{
					URL url = new URL(commonIndexRequest.getPlatform().getEndpointSingle() + "/" + fileRequest.getExternalId());
					if(fileRequest.getMimeType().contains("application/vnd.google-apps.spreadsheet")){
						url = new URL("https://www.googleapis.com/drive/v3/files/"+fileRequest.getExternalId()+"/export?mimeType=text/csv");
					}else if(fileRequest.getMimeType().contains("application/vnd.google-apps.document")){
						url = new URL("https://www.googleapis.com/drive/v3/files/"+fileRequest.getExternalId()+"/export?mimeType=text/plain");
					}
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setRequestProperty("Authorization", "Bearer " + gDriveResponseObj.getAccess_token());
					
					//Get Response  
					InputStream is = connection.getInputStream();
					BufferedReader rd = new BufferedReader(new InputStreamReader(is));
					StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
					String inputLine;
					while ((inputLine = rd.readLine()) != null) {
						response.append(inputLine);
						for(String str : inputLine.split(" ")){
							tokens.add(str);
						}
						//response.append("\r");
					}
					rd.close();
					if(fileRequest.getMimeType().contains("application/vnd.google-apps.document") || fileRequest.getMimeType().contains("application/vnd.google-apps.spreadsheet")){
						//now making the string again
						StringBuilder finalResponse = new StringBuilder();
						for(String value : tokens){
							finalResponse.append(value + " ");
					    }
						mapPlatformIdBody.put(fileRequest.getExternalId(), new IndexServiceResponseWorker(fileRequest.getTitle(),
								fileRequest.getKind(),
								fileRequest.getMimeType(),
								AsyncProcessWorker.GOOGLE_DRIVE,
								fileRequest.getExternalId(),
								"",
								commonIndexRequest.getSalesforce().getFilesLibraryId()));
						mapPlatformIdBody.get(fileRequest.getExternalId()).setIndex(finalResponse.toString());
					}
					/*
					if(fileRequest.getMimeType().contains("application/vnd.google-apps.spreadsheet")){
						Gson gson = new Gson();
						GoogleDriveFileResponseWorker gDriveFileResponseObj = gson.fromJson(response.toString(), GoogleDriveFileResponseWorker.class);
						mapPlatformIdBody.put(gDriveFileResponseObj.getId(), new IndexServiceResponseWorker(gDriveFileResponseObj.getTitle(),
																										gDriveFileResponseObj.getKind(),
																										gDriveFileResponseObj.getMimeType(),
																										AsyncProcessWorker.GOOGLE_DRIVE,
																										gDriveFileResponseObj.getId(),
																										gDriveFileResponseObj.getWebContentLink(),
																										commonIndexRequest.getSalesforce().getFilesLibraryId()));
						
						//For Google Drive Spread Sheets
						SelfParserUtilityWorker.readXLSXFile("https://docs.google.com/spreadsheets/export?id="+ gDriveFileResponseObj.getId() +"&exportFormat=xlsx", 
									gDriveFileResponseObj.getId(), 
									mapPlatformIdBody);
					}*/
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					if (connection != null) {
						connection.disconnect();
					}
				}
		}
	
	private static void getTheFinalTextData(GoogleDriveFileResponseWorker gDriveFileResponseObj, Map<String, IndexServiceResponseWorker> mapPlatformIdBody){
		HttpURLConnection connection = null;
		try{
			Set<String> tokens = new HashSet<String>();
			URL url = new URL(gDriveFileResponseObj.getExportLinks().getTextPlain());
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
		    //Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
		    String inputLine;
		    while ((inputLine = rd.readLine()) != null) {
		    	String [] tokensArray = inputLine.split(" ");
		    	tokens.addAll(Arrays.asList(tokensArray));
		    }
		    for(String value : tokens){
		    	response.append(value + " ");
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
