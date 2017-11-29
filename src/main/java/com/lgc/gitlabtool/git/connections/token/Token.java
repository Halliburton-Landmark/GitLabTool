package com.lgc.gitlabtool.git.connections.token;

public class Token {

    private String access_token;

    private String token_type;

    private String refresh_token;

    private String scope;

    private float created_at;

    public String getTokenWithType(){
        return token_type + " " + access_token;
    }
    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public float getCreated_at() {
        return created_at;
    }

    public void setCreated_at(float created_at) {
        this.created_at = created_at;
    }
}
