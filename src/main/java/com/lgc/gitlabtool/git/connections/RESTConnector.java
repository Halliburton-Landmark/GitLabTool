package com.lgc.gitlabtool.git.connections;

import java.util.Map;

public interface RESTConnector {
    String URL_MAIN_PART = "https://gitlab.com/api/v3";

    /**
     * Sends post request
     *
     * @param suffixForUrl suffix for adding to main URL
     * @param params for request
     * @param header the data to be added to header of request.
     *               if the header is not needed then pass null
     * @return {@link HttpResponseHolder} instance which contains response body, response code and header lines
     *         or <code>null</code>, if an error occurred in the request
     */
    HttpResponseHolder sendPost(String suffixForUrl, Map<String, String> params, Map<String, String> header);

    /**
     * Sends get request
     *
     * @param suffixForUrl suffix for adding to main URL
     * @param params for request
     * @param header the data to be added to header of request.
     *               if the header is not needed then pass null
     * @return {@link HttpResponseHolder} instance which contains response body, response code and header lines
     *         or <code>null</code>, if an error occurred in the request
     */
    HttpResponseHolder sendGet(String suffixForUrl, Map<String, String> params, Map<String, String> header);

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

}
