package fileforce.Controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

import com.google.gson.Gson;

import fileforce.Configuration.AppConfig;
import fileforce.Configuration.RabbitConfiguration;
import fileforce.Helper.FileForceUtility;
import fileforce.Helper.ParserUtils;
import fileforce.MapperWorker.CommonMapperWorker;
import fileforce.Model.Request.CommonIndexRequest;
import fileforce.Model.Response.ContentVersionResponseWorker;
import fileforce.Model.Response.GoogleDriveFilesResponseWorker;
import fileforce.Model.Response.IndexServiceResponseWorker;
import fileforce.Model.Response.SalesforceAuthTokenResponse;

@Configuration
public class AsyncProcessWorker {
	public static final String GOOGLE_DRIVE = "GoogleDrive";
	public static final String ERROR_MSG = "ERROR_MSG";
	public static final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
	
	public static void main(String[] args) {
	    ctx.register(AppConfig.class);
	    ctx.refresh();
	    //AsyncProcessWorker classObj =  new AsyncProcessWorker();
	    AsyncProcessWorker.createIndexJobListener();
    }
	
	public static void createIndexJobListener(){
		final ApplicationContext rabbitConfig = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
        final ConnectionFactory rabbitConnectionFactory = rabbitConfig.getBean(ConnectionFactory.class);
        final Queue rabbitQueue = rabbitConfig.getBean(Queue.class);
        final MessageConverter messageConverter = new SimpleMessageConverter();

        // create a listener container, which is required for asynchronous message consumption.
        // AmqpTemplate cannot be used in this case
        final SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
        listenerContainer.setConnectionFactory(rabbitConnectionFactory);
        listenerContainer.setQueueNames(rabbitQueue.getName());
        
        //CommonMapperWorker mapper = ctx.getBean(CommonMapperWorker.class);
        
        // set the callback for message handling
        listenerContainer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
            	final String str = (String) messageConverter.fromMessage(message);
            	System.out.println("Received from RabbitMQ: " + str);
            	Gson gson = new Gson(); 
            	final CommonIndexRequest commonRequest = gson.fromJson(str, CommonIndexRequest.class);
                // simply printing out the operation, but expensive computation could happen here
            	try{
            		if(commonRequest.getPlatform() != null && commonRequest.getPlatform().getPlatformName().equalsIgnoreCase(GOOGLE_DRIVE)){
                		if(commonRequest.getSalesforce().isInitial_sync()){
                			//Fetch All Google Drive Files
                			//Send the body without INDEX
                			GoogleDriveFilesResponseWorker gDriveFilesResponseWorkerObj = FileForceUtility.fetchAllGoogleDriveFiles(commonRequest);
                			List<IndexServiceResponseWorker> lstIndexResponses = new ArrayList<IndexServiceResponseWorker>();
                			for(GoogleDriveFilesResponseWorker.GoogleFile fileObj : gDriveFilesResponseWorkerObj.getFiles()){
                				IndexServiceResponseWorker tempObj = new IndexServiceResponseWorker(fileObj.getName(), 			
                																					fileObj.getMimeType(), 
                																					fileObj.getId(), 
                																					fileObj.getKind());
                				lstIndexResponses.add(tempObj);
                			}
                			makeHTTPCallToSalesforce(lstIndexResponses, commonRequest);
                		}else{
    	            		//MasterTableResponseWorker mTableResponseObj = mapper.getMasterDataWorker(commonRequest.getSalesforce().getOrgId());
    	        			//System.out.println("Received from RabbitMQ Schema Name : " + mTableResponseObj.getSchemaName());
    	        			//if(mTableResponseObj.getSchemaName() != null){
    	        				//Map<String, IndexServiceResponseWorker> mapPlatformIdAndResponse = runIndexingForEveryFile(commonRequest, mTableResponseObj, mapper);
    	        				//TODO
    	        				//Call the Salesforce rest service to dump the data.
                			Map<String, IndexServiceResponseWorker> mapPlatformIdAndResponse = runIndexingForEveryFile(commonRequest);
            				if(mapPlatformIdAndResponse != null){
            					List<IndexServiceResponseWorker> lstIndexResponses = new ArrayList<IndexServiceResponseWorker>(mapPlatformIdAndResponse.values());
            					System.out.println("lstIndexResponses size  =======" + lstIndexResponses.size());
            					makeHTTPCallToSalesforce(lstIndexResponses, commonRequest);
            				}
    	        			//}
                		}
            		}
            	}catch(Exception e){
            		System.out.println("In Exception == " + e.getMessage());
            	}
            }
        });

        // set a simple error handler
        listenerContainer.setErrorHandler(new ErrorHandler() {
            public void handleError(Throwable t) {
            	System.out.println("In Error Handler == " + t.getMessage());
            	t.printStackTrace();
            }
        });

        // register a shutdown hook with the JVM
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutting down Index Operation Worker");
                listenerContainer.shutdown();
            }
        });

        // start up the listener. this will block until JVM is killed.
        listenerContainer.start();
        System.out.println("Index Operation Worker started");
	}
	
	
	private static SalesforceAuthTokenResponse getJWTAuthToken(CommonIndexRequest commonRequest){
	    String header = "{\"alg\":\"RS256\"}";
	    String claimTemplate = "'{'\"iss\": \"{0}\", \"sub\": \"{1}\", \"aud\": \"{2}\", \"exp\": \"{3}\"'}'";

	    try {
	      StringBuffer token = new StringBuffer();
	      //Encode the JWT Header and add it to our string to sign
	      //token.append(Base64.encodeBase64URLSafeString(header.getBytes("UTF-8")));
	      token.append(Base64.encodeBase64URLSafeString(header.getBytes("UTF-8")));
	      AppConfig configNew = ctx.getBean(AppConfig.class);
	      //Separate with a period
	      token.append(".");

	      //Create the JWT Claims Object
	      String[] claimArray = new String[4];
	      claimArray[0] = configNew.getKey1()+"."+configNew.getKey2();
	      claimArray[1] = commonRequest.getSalesforce().getUserName();
	      claimArray[2] = commonRequest.getSalesforce().getSourceOrg();
	      claimArray[3] = Long.toString( ( System.currentTimeMillis()/1000 ) + 300);
	      MessageFormat claims;
	      claims = new MessageFormat(claimTemplate);
	      String payload = claims.format(claimArray);

	      //Add the encoded claims object
	      token.append(Base64.encodeBase64URLSafeString(payload.getBytes("UTF-8")));

	      //Load the private key from a keystore
	      KeyStore keystore = KeyStore.getInstance("JKS");
	      
	      String workingDir = System.getProperty("user.dir");
	      //System.out.println("Current working directory : " + workingDir + "===" + configNew.getDriverClassName() + "===" + configNew);
	      keystore.load(new FileInputStream(workingDir + "/fileforce-api-workersh/src/main/resources/static/"+configNew.getJksFileName()+".jks"), String.valueOf(configNew.getJksFilePassword()).toCharArray());
	      PrivateKey privateKey = (PrivateKey) keystore.getKey(configNew.getJksAlias(), String.valueOf(configNew.getJksAliasPassword()).toCharArray());
	      
	      //Sign the JWT Header + "." + JWT Claims Object
	      Signature signature = Signature.getInstance("SHA256withRSA");
	      signature.initSign(privateKey);
	      signature.update(token.toString().getBytes("UTF-8"));
	      String signedPayload = Base64.encodeBase64URLSafeString(signature.sign());

	      //Separate with a period
	      token.append(".");

	      //Add the encoded signature
	      token.append(signedPayload);
	      System.out.println(token.toString());
	      SalesforceAuthTokenResponse salesforceResponseObj = getAuthTokenFromSalesforce(token.toString(), commonRequest);
	      System.out.println("=======salesforceResponseObj=========" + salesforceResponseObj);
	      return salesforceResponseObj;
	    } catch (Exception e) {
	    	System.out.println("Error in getting JWT Bearer Token :==="  + e.getMessage());
	        e.printStackTrace();
	        return null;
	    }
	}
	
	private static SalesforceAuthTokenResponse getAuthTokenFromSalesforce(String signedJwtRequest, CommonIndexRequest commonRequest){
		  HttpURLConnection connection = null;
			try {
				String payload = "grant_type=" + URLEncoder.encode("urn:ietf:params:oauth:grant-type:jwt-bearer", "UTF-8");
				payload += "&assertion=" + signedJwtRequest;
				
			    URL url = new URL(commonRequest.getSalesforce().getSourceOrg() + "/services/oauth2/token");
			    connection = (HttpURLConnection) url.openConnection();
			    connection.setRequestMethod("POST");
			    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			    connection.setRequestProperty("Content-length", payload.getBytes().length + "");
			    connection.setUseCaches(false);
			    connection.setDoOutput(true);
			    connection.setDoInput(true);
			    
			    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
		        outputStream.writeBytes(payload);
		        outputStream.flush();
		        
		        int rc = connection.getResponseCode();
		        InputStreamReader is = null;
		        if(rc == 200){
		        	is = new InputStreamReader(connection.getInputStream());
		        }else{
		        	is = new InputStreamReader(connection.getErrorStream());
		        }
		        BufferedReader rd = new BufferedReader(is);
		        StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
			    String line;
			    while ((line = rd.readLine()) != null) {
			      response.append(line);
			      response.append('\r');
			    }
			    Gson gson = new Gson();
			    SalesforceAuthTokenResponse salesforceResponseObj = gson.fromJson(response.toString(), SalesforceAuthTokenResponse.class);
			    rd.close();
			    System.out.println("salesforceResponseObj======" + salesforceResponseObj);
			    return salesforceResponseObj;
			  } catch (Exception e) {
			    e.printStackTrace();
			    //return null;
			  } finally {
			    if (connection != null) {
			      connection.disconnect();
			    }
			  }
			return null;
	  }
	
	private static void makeHTTPCallToSalesforce(List<IndexServiceResponseWorker> lstIndexResponses, CommonIndexRequest commonRequest){
		HttpURLConnection connection = null;
		try{
			SalesforceAuthTokenResponse sAuthObj = getJWTAuthToken(commonRequest);
			if(sAuthObj != null && sAuthObj.getAccess_token() != null){
				URL url = new URL(commonRequest.getSalesforce().getUpdateFilesURL());
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Authorization", "Bearer " + sAuthObj.getAccess_token());
				connection.setRequestProperty("Content-Type", "application/json");
			    connection.setUseCaches(false);
			    connection.setDoOutput(true);
			    
			    JSONArray listParsedFiles = new JSONArray();
			    for(IndexServiceResponseWorker tempObj : lstIndexResponses){
			    	JSONObject obj = new JSONObject();
			    	obj.put("platform_id", tempObj.getPlatform_id());
				    obj.put("type", tempObj.getType());
				    obj.put("mimeType", tempObj.getMimeType());
				    obj.put("platform", tempObj.getPlatform());
				    obj.put("url", tempObj.getUrl());
				    obj.put("name", tempObj.getName());
				    String finalString = tempObj.getIndex() != null ? tempObj.getIndex().replaceAll("[^a-z A-Z0-9]","") : "";
			    	obj.put("index", finalString);
				    //1AaC91q99CTfqa0XRAbhmMktLl0WYpAj5U4yxAtD0mD4
				    //1JNnny5dql9cRdmuqqobBCvBlHvv_rTuS3M1CrSzjX3E
			    	//app  working  APP Configuration upload app  your ﻿Partner Define dev enable per 1  if flow app in authorizes want signed personal it as JKS following domain Package redirect_uri 2  environment  A be images  functionality URL current file Generate by 3  Download connecte a it  store too  the connected first  self 4  to scopes Managed open Add Once project used salesforce FileForce”  5  This bearer from needs add which new test below need created JWT profiles this IMPORTANT Management  Please 6  Certificate salesforce  “Authorize settings  Org folder  certificate link show https://login salesforce com/services/oauth2/authorize?client_id=3MVG95NPsF2gwOiNgyqtZADg2uWtpzF0B4X5wc5j ZX2VGvKWpmhEQp0U7Zhv2usa9bhY3lb6uGmLOZHLavbY&redirect_uri=https://fileforce api herokuapp com/index html&response_type=code&source=https://ap5 salesforce com Source we button Create and Auth of Salesforce will org custom start say message We Oauth with Client_Id user org  certificate
				    listParsedFiles.add(obj);
				    //System.out.println("===== new Index JSON===" + obj.toJSONString() + " ====" + tempObj.getPlatform_id());
			    }
		        String json2 = listParsedFiles.toJSONString();
				DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
		        outputStream.writeBytes(json2);
		        outputStream.flush();
		        
			    System.out.println(connection.getResponseCode() + "====" + connection.getResponseMessage());
			    
			    int rc = connection.getResponseCode();
		        InputStreamReader is = null;
		        if(rc == 200){
		        	is = new InputStreamReader(connection.getInputStream());
		        }else{
		        	is = new InputStreamReader(connection.getErrorStream());
		        }
			    
		        BufferedReader rd = new BufferedReader(is);
		        StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
			    String line;
			    while ((line = rd.readLine()) != null) {
			      response.append(line);
			      response.append('\r');
			    }
	            rd.close();
	            System.out.println("Response ======="+ response.toString());
			}else{
				System.out.println("sAuthObj=== " +sAuthObj.getError_description());
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("ERROR  ===makeHTTPCallToSalesforce=== " +e.getMessage());
		}finally{
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	
	//method to fetch every file and parse it with common parser
	private static Map<String, IndexServiceResponseWorker> runIndexingForEveryFile(CommonIndexRequest commonRequest){
		//List<ContentVersionResponseWorker> listGoogleDriveFiles  = mapper.getContentVersionData(new ContentVersionRequest(mTableResponseObj.getSchemaName(), 1));
		GoogleDriveFilesResponseWorker gDriveFilesResponseWorkerObj = FileForceUtility.fetchAllGoogleDriveFiles(commonRequest);
		if(gDriveFilesResponseWorkerObj != null){
			List<ContentVersionResponseWorker> listGoogleDriveFiles = new ArrayList<ContentVersionResponseWorker>();
			//introducing a count variable for testing purposes
			int countRecords = 0;
			String testPlatformIds = "";
			for(GoogleDriveFilesResponseWorker.GoogleFile fileObj : gDriveFilesResponseWorkerObj.getFiles()){
				ContentVersionResponseWorker tempObj = new ContentVersionResponseWorker();
				tempObj.setExternalId(fileObj.getId());
				tempObj.setTitle(fileObj.getName());
				tempObj.setKind(fileObj.getKind());
				tempObj.setMimeType(fileObj.getMimeType());
				listGoogleDriveFiles.add(tempObj);
				testPlatformIds += fileObj.getId() + "===";
				countRecords++;
				if(countRecords == 5){
					break;
				}
			}
			if(listGoogleDriveFiles != null && listGoogleDriveFiles.size() > 0){
				System.out.println("Size of the files from Google Drive : " + listGoogleDriveFiles.size());
				System.out.println("Test Platform Ids : " + testPlatformIds);
				Map<String, IndexServiceResponseWorker> mapPlatformIdAndResponse = ParserUtils.parsefiles(commonRequest, listGoogleDriveFiles);
				return mapPlatformIdAndResponse;
			}
		}
		return null;
	}
}

/*
 
 SAMPLE JSON
 
 {  
    "salesforce":{  
       "updateFilesURL":"https://ff-ts-dev-ed.my.salesforce.com/services/apexrest/MetafileService/updateMetafiles",
       "sessionId":"SESSION_ID_REMOVED",
       "filesLibraryId":null,
       "orgId":"00D1r000000TYde",
       "userName":"manmeetitsme1987+fileforce-techspike@gmail.com",
       "sourceOrg":"https://login.salesforce.com",
       "initial_sync":true
    },
    "platform":{  
       "refreshToken":"1/KuWjejdKhpbisf_cRRvJ-bUu35v_JgLiTxePAD4X1oI",
       "platformName":"GoogleDrive",
       "token_endpoint":"https://www.googleapis.com/oauth2/v4/token",
       "clientId":"619983446033-4d4i2ekkmfal2r29ngjegkc0t53qascs.apps.googleusercontent.com",
       "clientSecret":"zDJjNrgtxiVqOuy_C8pajVVm",
       "clientRedirectURI":"https://ff-ts-dev-ed.lightning.force.com/c/GoogleOAuthCompletion.app",
       "endpointAll":"https://www.googleapis.com/drive/v3/files",
       "endpointSingle":"https://www.googleapis.com/drive/v2/files",
       "platform_file_id":""
    }
 }
 
 *
 *
 */
