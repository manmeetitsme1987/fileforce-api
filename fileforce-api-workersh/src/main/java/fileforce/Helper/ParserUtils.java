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
			try{
				URL url = new URL(commonIndexRequest.getPlatform().getEndpoint() + "/" + fileRequest.getExternalId());
				//URL url = new URL(commonIndexRequest.getPlatform().getEndpoint() + "/1jTVywFwB2BoVSRgGeAoPKOpUyP_3F3K6K6NHOCFPdw8");
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Authorization", "Bearer " + gDriveResponseObj.getAccess_token());
				
				//Get Response  
				InputStream is = connection.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
				String inputLine, tempFileType = "";
				while ((inputLine = rd.readLine()) != null) {
					if(inputLine.contains("text/plain")){
						inputLine = inputLine.replaceAll("text/plain", "textPlain");
						tempFileType = "text/plain";
					}else if(inputLine.contains("text/csv")){
						inputLine = inputLine.replaceAll("text/csv", "textCSV");
						tempFileType = "text/csv";
					}else if(inputLine.contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")){
						inputLine = inputLine.replaceAll("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "applicationSpreadSheet");
						tempFileType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
					}else if(inputLine.contains("application/pdf")){
						inputLine = inputLine.replaceAll("application/pdf", "applicationPDF");
						tempFileType = "application/pdf";
					}else if(inputLine.contains("application/vnd.openxmlformats-officedocument.presentationml.presentation")){
						inputLine = inputLine.replaceAll("application/vnd.openxmlformats-officedocument.presentationml.presentation", "applicationPPT");
						tempFileType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
					}
					response.append(inputLine);
					response.append("\r");
				}
				rd.close();
				Gson gson = new Gson();
				GoogleDriveFileResponseWorker gDriveFileResponseObj = gson.fromJson(response.toString(), GoogleDriveFileResponseWorker.class);
				mapPlatformIdBody.put(gDriveFileResponseObj.getId(), new IndexServiceResponseWorker(gDriveFileResponseObj.getTitle(),
																								gDriveFileResponseObj.getKind(),
																								tempFileType,
																								AsyncProcessWorker.GOOGLE_DRIVE,
																								gDriveFileResponseObj.getId(),
																								gDriveFileResponseObj.getWebContentLink(),
																								commonIndexRequest.getSalesforce().getFilesLibraryId()));
				//not supporting Images for now
				String fileTitle = gDriveFileResponseObj.getTitle().toLowerCase();
				if(!(fileTitle.contains("jpeg") || fileTitle.contains("jpg"))){
					if(fileTitle.contains("doc") || fileTitle.contains("docx")){
						if(gDriveFileResponseObj.getExportLinks() != null && gDriveFileResponseObj.getExportLinks().getTextPlain() != null){
							getTheFinalTextData(gDriveFileResponseObj, mapPlatformIdBody);
						}
					}else if(gDriveFileResponseObj.getMimeType().contains("spreadsheet")){
						SelfParserUtilityWorker.readXLSXFile(gDriveFileResponseObj.getExportLinks().getApplicationSpreadSheet(), 
														gDriveFileResponseObj.getId(), 
														mapPlatformIdBody);
				
					}else if(gDriveFileResponseObj.getMimeType().contains("applicationPDF")){
						SelfParserUtilityWorker.readPDFFile(gDriveFileResponseObj.getWebContentLink(), 
														gDriveFileResponseObj.getId(), 
														mapPlatformIdBody);
					}else if(gDriveFileResponseObj.getMimeType().contains("applicationPPT")){
						//if(gDriveFileResponseObj.getTitle().contains("pptx")){
							SelfParserUtilityWorker.readPPTXFile(gDriveFileResponseObj.getWebContentLink(), 
									gDriveFileResponseObj.getId(), 
									mapPlatformIdBody);
						//}else{
						//	SelfParserUtilityWorker.readPPTFile(gDriveFileResponseObj.getWebContentLink(), 
						//			gDriveFileResponseObj.getId(), 
						//			mapPlatformIdBody);
						//}
					}
				}
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
