package com.lgc.gitlabtool.git.connections;

import java.util.List;
import java.util.Map;

/**
 * This class stores the parameters of HTTP response from server
 * 
 * @author Igor Khlaponin
 */
public class HttpResponseHolder {

    private Map<String, List<String>> headerLines;
    private Object body;
    private int responseCode;

    public HttpResponseHolder(Map<String, List<String>> headerLines, Object body) {
        this.headerLines = headerLines;
        this.body = body;
    }

    public HttpResponseHolder(Map<String, List<String>> headerLines, Object body, int responseCode) {
        this(headerLines, body);
        this.responseCode = responseCode;
    }

    public Map<String, List<String>> getHeaderLines() {
        return headerLines;
    }

    public void setHeaderLines(Map<String, List<String>> headerLines) {
        this.headerLines = headerLines;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

}
