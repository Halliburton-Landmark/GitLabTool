package com.lgc.gitlabtool.git.connections;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lgc.gitlabtool.git.util.RequestType;
import com.lgc.gitlabtool.git.util.URLManager;

class RESTConnectorImpl implements RESTConnector {

    private static final Logger _logger = LogManager.getLogger(RESTConnectorImpl.class);
    private String _urlMainPart;

    public RESTConnectorImpl() {}

    /**
     * Opens the HTTP connection to the defined URL 
     * 
     * @param url - URL to send connection
     * @return created connection for defined URL
     * @throws Exception if URL isn't valid
     */
    private HttpsURLConnection getConnection(URL url) throws Exception {
        return (HttpsURLConnection) url.openConnection();
    }

    @Override
    public HttpResponseHolder sendPost(String suffixForUrl, Map<String, String> params, Map<String, String> header) {
        return sendRequest(suffixForUrl, params,header, RequestType.POST);
    }

    @Override
    public HttpResponseHolder sendGet(String suffixForUrl, Map<String, String> params, Map<String, String> header) {
        return sendRequest(suffixForUrl, params,header, RequestType.GET);
    }

    /**
     * Sends request
     *
     * @param suffixForUrl suffix for adding to main URL
     * @param params for request
     * @param header the data to be added to header of request.
     *               if the header is not needed then pass null
     * @param request - for example: RequestType.GET or RequestType.POST etc.
     *
     * @return {@link HttpResponseHolder} instance which contains response body, response code and header lines
     *         or <code>null</code>, if an error occurred in the request
     */
    private HttpResponseHolder sendRequest(String suffixForUrl, Map<String, String> params, Map<String, String> header, RequestType request) {
        HttpsURLConnection connection = null;
        HttpResponseHolder responseHolder = new HttpResponseHolder();
        try {
            URL url = new URL(_urlMainPart + suffixForUrl);
            connection = getConnection(url);

            setHTTPRequestHeader(header, connection);
            connection.setRequestMethod(request.toString());

            if (params != null) {
                String urlParameters = formParameters(params);
                // Send post request
                connection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
            }

            responseHolder.setResponseCode(connection.getResponseCode());
            responseHolder.setResponseMessage(connection.getResponseMessage());
            _logger.info("Sending '" + request +"' request to URL : " + url.toString());
            _logger.info("Response Code : " + connection.getResponseCode());
            _logger.info("Response Message : " + connection.getResponseMessage());


            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            responseHolder.setHeaderLines(connection.getHeaderFields());
            responseHolder.setBody(response.toString());
            _logger.info(response.toString());

        } catch (Exception e) {
            _logger.error("Error sending request: " + e.getMessage());
        } finally {
            if (connection != null && connection.getExpiration() == 0) {
                connection.disconnect();
            }
        }
        return responseHolder;
    }

    private void setHTTPRequestHeader(Map<String, String> header, HttpsURLConnection con) {
        if (header != null) {
            header.entrySet().forEach(e -> con.setRequestProperty(e.getKey(), e.getValue()));
        }
    }

    private String formParameters(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?,?> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                urlEncodeUTF8(entry.getKey().toString()),
                urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }

    private Object urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            _logger.error("Error encoding URL: " + e.getMessage());
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public void setUrlMainPart(String urlMainPart) {
        if (urlMainPart != null && URLManager.isURLValid(urlMainPart)) {
            this._urlMainPart = urlMainPart;
        }
    }

    @Override
    public String getUrlMainPart() {
        return _urlMainPart;
    }

}
