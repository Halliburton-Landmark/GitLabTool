package com.lgc.gitlabtool.git.connections;

import java.util.List;
import java.util.Map;

/**
 * This class stores the parameters of HTTP response from server
 * 
 * @author Igor Khlaponin
 */
public class HttpResponseHolder {

    private Map<String, List<String>> _headerLines;
    private Object _body;
    private int _responseCode;
    private String _responseMessage;

    public HttpResponseHolder() {
        
    }

    public HttpResponseHolder(Map<String, List<String>> headerLines, Object body) {
        this._headerLines = headerLines;
        this._body = body;
    }

    public HttpResponseHolder(Map<String, List<String>> headerLines, Object body, int responseCode) {
        this(headerLines, body);
        this._responseCode = responseCode;
    }

    /**
     * Returns the header lines of HTTP response from server
     * 
     * @return collection of the header lines
     */
    public Map<String, List<String>> getHeaderLines() {
        return _headerLines;
    }

    /**
     * Sets the header lines of HTTP response from server
     * 
     * @param headerLines - HTTP response header lines
     */
    public void setHeaderLines(Map<String, List<String>> headerLines) {
        this._headerLines = headerLines;
    }

    /**
     * @return the body of the server response
     */
    public Object getBody() {
        return _body;
    }

    /**
     * Sets the body of the server HTTP response
     * 
     * @param body - body of the server response
     */
    public void setBody(Object body) {
        this._body = body;
    }

    /**
     * Returns the response code of the server
     * 
     * @return response code
     */
    public int getResponseCode() {
        return _responseCode;
    }

    /**
     * Sets the response code of the server
     * 
     * @param responseCode
     */
    public void setResponseCode(int responseCode) {
        this._responseCode = responseCode;
    }

    /**
     * Return the response message of the server
     * 
     * @return response message
     */
    public String getResponseMessage() {
        return _responseMessage;
    }

    /**
     * Set the response message of the server
     * 
     * @param _responseMessage
     */
    public void setResponseMessage(String responseMessage) {
        this._responseMessage = responseMessage;
    }

}
