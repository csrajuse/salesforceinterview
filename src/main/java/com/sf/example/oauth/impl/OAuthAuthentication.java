package com.sf.example.oauth.impl;

import com.sf.example.oauth.Authenticate;
import com.sf.example.repository.TokenRepository;
import com.sf.example.rest.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OAuthAuthentication implements Authenticate {

    private static final String authType = "oauth";

    @Value("${oauth_username}")
    private String oauthUsername;

    @Value("${oauth_password}")
    private String oauthPassword;

    @Value("${oauth_customer_key}")
    private String oauthCustomerKey;

    @Value("${oauth_client_secret}")
    private String oauthClientSecret;

    @Value("${salesforce_loginhost}")
    private String loginHost;

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public String getAccessToken() {
        try {
            JSONParser jsonParser = new JSONParser();
            // Set up an HTTP client that makes a connection to REST API.

            Map<String,String> paramsMap = new HashMap<>();
            paramsMap.put("grant_type", "password");
            paramsMap.put("username", oauthUsername);
            paramsMap.put("password", oauthPassword);
            paramsMap.put("client_id", oauthCustomerKey);
            paramsMap.put("client_secret", oauthClientSecret);

            HttpPost httpPost = HttpUtils.generatePostRequestCient(loginHost,"/services/oauth2/token",paramsMap,new HashMap<>());

            // Set up an HTTP client that makes a connection to REST API.
            DefaultHttpClient client = new DefaultHttpClient();
            HttpParams params = client.getParams();
            HttpClientParams.setCookiePolicy(params, CookiePolicy.BROWSER_COMPATIBILITY);
            params.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 30000);

            // Execute the request.
            HttpResponse response = client.execute(httpPost);
            Map<String, String> oauthLoginResponse = (Map<String, String>)
                    jsonParser.parse(EntityUtils.toString(response.getEntity()));
            String accessToken = oauthLoginResponse.get("access_token");
            //store it in the database before returning it.
            tokenRepository.storeToken(accessToken,authType);

            return oauthLoginResponse.get("access_token");
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getStoredToken() {
        return tokenRepository.getStoredToken(authType);
    }
}
