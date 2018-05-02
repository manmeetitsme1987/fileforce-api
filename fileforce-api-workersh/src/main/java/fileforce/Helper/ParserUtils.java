package fileforce.Helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import fileforce.Model.Request.CommonIndexRequest;
import fileforce.Model.Request.GoogleDriveRequestWorker;
import fileforce.Model.Response.GoogleDriveAuthResponseWorker;
import fileforce.Model.Response.GoogleDriveFileResponseWorker;

public class ParserUtils {
	
	public static void parsefiles(CommonIndexRequest commonRequest, List<GoogleDriveRequestWorker> gDriveFileList){
		GoogleDriveAuthResponseWorker gDriveResponseObj = getDriveDataWithRefreshToken(commonRequest);
		Map<String, String> mapPlatformIdBody = new HashMap();
		if(gDriveResponseObj.getAccess_token() != null){
			for(GoogleDriveRequestWorker fileRequest : gDriveFileList){
				getIndividualFileData(fileRequest, commonRequest, gDriveResponseObj, mapPlatformIdBody);
			}
		}
		Gson gson = new Gson(); 
		String json = gson.toJson(mapPlatformIdBody);
		System.out.println(json + "=== converted JSON");
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
	
	
	public static void getIndividualFileData(GoogleDriveRequestWorker fileRequest, 
												CommonIndexRequest commonIndexRequest,
												GoogleDriveAuthResponseWorker gDriveResponseObj,
												Map<String, String> mapPlatformIdBody){
			HttpURLConnection connection = null;
			try{
				URL url = new URL(commonIndexRequest.getPlatform().getEndpoint() + "/" + fileRequest.getExternalId());
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
				GoogleDriveFileResponseWorker gDriveFileResponseObj = gson.fromJson(response.toString(), GoogleDriveFileResponseWorker.class);
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
				}
				}
				//System.out.println(response.toString());		    
				//return response.toString();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if (connection != null) {
					connection.disconnect();
				}
			}
		}
	
	
	private static void getTheFinalTextData(GoogleDriveFileResponseWorker gDriveFileResponseObj, Map<String, String> mapPlatformIdBody){
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
		    mapPlatformIdBody.put(gDriveFileResponseObj.getId(), response.toString());
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
}
