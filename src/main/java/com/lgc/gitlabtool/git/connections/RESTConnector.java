package com.lgc.gitlabtool.git.connections;

import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.lgc.gitlabtool.git.services.ProjectServiceImpl;

public interface RESTConnector {
    String URL_MAIN_PART = "https://gitlab.com/api/v3";

    /**
     * Sends post request
     *
     * @param suffixForUrl suffix for adding to main URL
     * @param params for request
     * @param header the data to be added to header of request.
     *               if the header is not needed then pass null
     * @return String with data or null, if an error occurred in the request
     */
    Object sendPost(String suffixForUrl, Map<String, String> params, Map<String, String> header);

    /**
     * Sends get request
     *
     * @param suffixForUrl suffix for adding to main URL
     * @param params for request
     * @param header the data to be added to header of request.
     *               if the header is not needed then pass null
     * @return String with data or null, if an error occurred in the request
     */
    Object sendGet(String suffixForUrl, Map<String, String> params, Map<String, String> header);

    /**
     * Sets the main part of server URL to the <code>RESTConnector</code>
     *
     * @param url the main part of the server URL
     */
    void setUrlMainPart(String url);

    /**
     * Returns the main part of server URL
     *
     * @return main part of server URL
     */
    String getUrlMainPart();
    
    /**
     * Returns the count of pages with projects after sending the request with 
     * set <code>per_page</code> parameter
     * <p>
     * Count of pages will be calculated by <code>per_page</code> value
     * <p>
     * Could be gotten only after sending the request
     * 
     * @return count of pages with max count of projects on it or <code>1</code> if connection does not exist
     */
    int getCountOfPages();
}
