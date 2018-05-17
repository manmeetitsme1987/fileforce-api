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
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

import fileforce.Model.Request.CommonIndexRequest;
import fileforce.Model.Response.ContentVersionResponse;
import fileforce.Model.Response.GoogleDriveAuthResponse;
import fileforce.Model.Response.GoogleDriveFileResponse;
import fileforce.Model.Response.IndexServiceResponse;
import fileforce.Service.CommonService;

public class ParserUtilsWeb {
	
	public static Map<String, IndexServiceResponse> parsefiles(CommonIndexRequest commonRequest, ContentVersionResponse contentVersionFile){
		GoogleDriveAuthResponse gDriveResponseObj = getDriveDataWithRefreshToken(commonRequest);
		Map<String, IndexServiceResponse> mapPlatformIdBody = new HashMap();
		if(gDriveResponseObj.getAccess_token() != null){
			if(!contentVersionFile.getExternalId().isEmpty()){
				System.out.println("Processing ======= file Name ====" + contentVersionFile.getTitle());
				getIndividualFileData(contentVersionFile, commonRequest, gDriveResponseObj, mapPlatformIdBody);
				//break;
			}
		}
		Gson gson = new Gson(); 
		String json = gson.toJson(mapPlatformIdBody);
		System.out.println("converted JSON=========" + json + "=== converted JSON");
		return mapPlatformIdBody;
	}
	
	//method to get the access token
	private  static GoogleDriveAuthResponse getDriveDataWithRefreshToken(CommonIndexRequest commonRequest){
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
		    GoogleDriveAuthResponse gDriveResponseObj = gson.fromJson(response.toString(), GoogleDriveAuthResponse.class);
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
	
	
	public static void getIndividualFileData(ContentVersionResponse fileRequest, 
												CommonIndexRequest commonIndexRequest,
												GoogleDriveAuthResponse gDriveResponseObj,
												Map<String, IndexServiceResponse> mapPlatformIdBody){
			HttpURLConnection connection = null;
			try{
				URL url = new URL(commonIndexRequest.getPlatform().getEndpoint() + "/" + commonIndexRequest.getPlatform().getPlatform_file_id());
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
				GoogleDriveFileResponse gDriveFileResponseObj = gson.fromJson(response.toString(), GoogleDriveFileResponse.class);
				mapPlatformIdBody.put(gDriveFileResponseObj.getId(), new IndexServiceResponse(gDriveFileResponseObj.getTitle(),
																									gDriveFileResponseObj.getKind(),
																									tempFileType,
																									CommonService.GOOGLE_DRIVE,
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
						SelfParserUtility.readXLSXFile(gDriveFileResponseObj.getExportLinks().getApplicationSpreadSheet(), 
														gDriveFileResponseObj.getId(), 
														mapPlatformIdBody);
				
					}else if(gDriveFileResponseObj.getMimeType().contains("applicationPDF")){
						SelfParserUtility.readPDFFile(gDriveFileResponseObj.getWebContentLink(), 
														gDriveFileResponseObj.getId(), 
														mapPlatformIdBody);
					}else if(gDriveFileResponseObj.getMimeType().contains("applicationPPT")){
						//if(gDriveFileResponseObj.getTitle().contains("pptx")){
							SelfParserUtility.readPPTXFile(gDriveFileResponseObj.getWebContentLink(), 
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
	
	
	private static void getTheFinalTextData(GoogleDriveFileResponse gDriveFileResponseObj, Map<String, IndexServiceResponse> mapPlatformIdBody){
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
