package com.ystrazhko.git.connections;

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

}
