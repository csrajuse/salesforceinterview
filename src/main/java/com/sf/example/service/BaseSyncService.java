package com.sf.example.service;

import com.sf.example.oauth.Authenticate;
import com.sf.example.repository.AnyObjectRepository;
import com.sf.example.rest.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class BaseSyncService {

    @Autowired
    private Authenticate authenticate;

    @Value("${salesforce_loginhost}")
    private String baseUrl;

    @Autowired
    private GenericService genericService;

    @Autowired
    private AnyObjectRepository anyObjectRepository;

    private static final String LAST_MODIFIED_QUERY_FOR_ID = "select id from %s where lastmodifieddate>%s";

    private static final Logger logger = LoggerFactory.getLogger("BaseSyncService");

    private static final String dateFormat = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";
    private static final String toDateFormat = "YYYY-MM-dd'T'hh:mm:ss'Z'";

    /**
     *
     * @param objectName like Account , Contacts etc
     * @param datetimeStr the datetimeStr which is basically the last modified date of the last got batch.
     * @return
     */
    protected List<Map<String,String>> syncObjectFromSalesForce(String objectName,String datetimeStr){

            JSONParser jsonParser = new JSONParser();
            List<String> ids = getIds(objectName, datetimeStr);

            List<Map<String, String>> retList = new ArrayList<>();
            String oauthStoredToken = authenticate.getStoredToken();
            ids.forEach(id -> {
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("auth_token", oauthStoredToken);
                    Map<String, String> headerParams = new HashMap<>();
                    headerParams.put("Authorization", "Bearer " + oauthStoredToken);
                    HttpGet httpGet = HttpUtils.generateGetRequest(baseUrl, "/services/data/v49.0/sobjects/"+objectName+"/" + id, params, headerParams);

                    // Set up an HTTP client that makes a connection to REST API.
                    HttpResponse response = getHttpClient().execute(httpGet);

                    if(response.getStatusLine().getStatusCode() == 200){
                        Map<String,String> objectsToBeSaved = new HashMap<>();
                        Map<String,Object> objectMap = (Map<String,Object>)jsonParser.parse(EntityUtils.toString(response.getEntity()));

                        objectMap.forEach((key,value)->{
                            if(value!=null && value instanceof String)
                                objectsToBeSaved.put(key,value.toString());
                            else
                                objectsToBeSaved.put(key,null);
                        });
                        retList.add(objectsToBeSaved);
                    }else{
                        throw new RuntimeException("ERROR : "+response.getStatusLine().getStatusCode());
                    }
                }catch(Exception ex){
                    throw new RuntimeException(ex);
                }
            });

            return retList;

    }

    List<String> getIds(String objectName,String datetimeStr){
        try {
            //Get the account objects that are modified after the date.
            String queryForIds = String.format(LAST_MODIFIED_QUERY_FOR_ID, objectName, datetimeStr);

            //We do the call assuming that this token is valid, if it is not valid then we need to get a new token and call this method again.
            String oauthStoredToken = authenticate.getStoredToken();

            Map<String, String> params = new HashMap<>();
            params.put("auth_token", oauthStoredToken);
            params.put("q", queryForIds);
            Map<String, String> headerParams = new HashMap<>();
            headerParams.put("Authorization", "Bearer " + oauthStoredToken);

            HttpGet httpGet = HttpUtils.generateGetRequest(baseUrl, "/services/data/v49.0/query/", params, headerParams);

            JSONParser jsonParser = new JSONParser();
            // Set up an HTTP client that makes a connection to REST API.
            HttpResponse response = getHttpClient().execute(httpGet);

            if(response.getStatusLine().getStatusCode() == 200){
                //The response is successful.
                List<String> listOfIds = new ArrayList<>();
                Map<String,Object> returnObjectMap = (Map<String,Object>)jsonParser.parse(EntityUtils.toString(response.getEntity()));
                returnObjectMap.forEach((key,value)->{
                    if(key.equals("records")){
                        ((JSONArray)value).stream().forEach(ele->((Map<String,Object>)ele).forEach((key1,value1)->{
                            if(key1.equals("Id")){
                                listOfIds.add((String)value1);
                            }
                        }));
                    }
                });

                return listOfIds;
            }else{
                throw new RuntimeException("ERROR CODE : "+response.getStatusLine().getStatusCode());
            }
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public void syncObjectToSalesforce(String objectName,String datetimeStr){
        try {
            List<Map<String,String>> allObjectsList = anyObjectRepository.fetchAllObjects(objectName);
            SimpleDateFormat revievedDateFormat = new SimpleDateFormat(dateFormat);
            List<Map<String,String>> lastModifiedDatesList = allObjectsList.stream()
                    .filter(map->{
                        try {
                            Date modifiedDate = revievedDateFormat.parse(map.get("lastModifiedDate"));
                            if(modifiedDate.after(revievedDateFormat.parse(datetimeStr))){
                                return true;
                            }
                            return false;
                        }catch(ParseException ex){
                            logger.error("parse exception:"+ex.getMessage());
                        }
                        return false;
                    }).collect(Collectors.toList());

            //Now sync to salesforce.
            String oauthStoredToken = authenticate.getStoredToken();
            lastModifiedDatesList.forEach(map->{
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("auth_token", oauthStoredToken);
                    Map<String, String> headerParams = new HashMap<>();
                    headerParams.put("Authorization", "Bearer " + oauthStoredToken);

                    HttpPost httpPost = HttpUtils.generatePostRequestCient(baseUrl, "/services/data/v49.0/sobjects/" + objectName, params, headerParams);
                    JSONObject jsonObject = new JSONObject(map);
                    StringEntity entity = new StringEntity(jsonObject.toJSONString());
                    httpPost.setEntity(entity);
                    HttpResponse response = getHttpClient().execute(httpPost);
                    if(response.getStatusLine().getStatusCode() != 200){
                        throw new Exception("Error posting http request");
                    }

                }catch(Exception ex){
                    logger.error(ex.getMessage());
                }
            });

        }catch(Exception ex){
            logger.error(ex.getMessage());
        }
    }

    HttpClient getHttpClient(){
        DefaultHttpClient client = new DefaultHttpClient();
        HttpParams httpParams = client.getParams();
        HttpClientParams.setCookiePolicy(httpParams, CookiePolicy.BROWSER_COMPATIBILITY);
        httpParams.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 30000);

        return client;
    }
}
