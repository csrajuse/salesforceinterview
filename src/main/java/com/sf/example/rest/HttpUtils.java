package com.sf.example.rest;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HttpUtils {

    public static final HttpPost generatePostRequestCient(String baseUrl,String resource,Map<String,String> bodyParams,Map<String,String> headerParams)
    throws UnsupportedEncodingException {

        String url = baseUrl + resource;
        HttpPost httpPost = new HttpPost(url);
        // The request body must contain these 5 values.
        List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
        bodyParams.forEach((key,value)->{
            parametersBody.add(new BasicNameValuePair(key, value));
        });

        headerParams.forEach((key,value)->{
           httpPost.addHeader(key,value);
        });

        httpPost.setEntity(new UrlEncodedFormEntity(parametersBody, HTTP.UTF_8));

        return httpPost;
    }

    public static final HttpGet generateGetRequest(String baseUrl, String resource, Map<String,String> bodyParams, Map<String,String> headerParams){

        // Set up an HTTP client that makes a connection to REST API.
        DefaultHttpClient client = new DefaultHttpClient();
        HttpParams params = client.getParams();
        HttpClientParams.setCookiePolicy(params, CookiePolicy.BROWSER_COMPATIBILITY);
        params.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 30000);

        String url = baseUrl + resource;

        List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
        bodyParams.forEach((key,value)->{
            parametersBody.add(new BasicNameValuePair(key, value));
        });
        String queryString = URLEncodedUtils.format(parametersBody, HTTP.UTF_8);
        HttpGet httpGet = new HttpGet(url+"?"+queryString);
        headerParams.forEach((key,value)->{
            httpGet.addHeader(key,value);
        });

        return httpGet;
    }
}
