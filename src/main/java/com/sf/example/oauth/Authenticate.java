package com.sf.example.oauth;
public interface Authenticate {
    /**
     * Getting the token from Salesforce. This is only required when the stored token is no more valid or there is no token in the database.
     * @return
     */
    String getAccessToken();

    /**
     * This is the access token that is stored in the database for oauth
     * @return
     */
    String getStoredToken();
}
