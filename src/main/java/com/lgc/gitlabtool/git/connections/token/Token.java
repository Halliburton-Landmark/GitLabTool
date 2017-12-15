package com.lgc.gitlabtool.git.connections.token;

/**
 * This class keeps data about a OAuth2 token.
 *
 * @author Pavlo Pidhorniy
 */
public class Token {

    /**
     * The value of a OAuth2 token
     **/
    private String access_token;

    /**
     * The type of a token
     **/
    private String token_type;

    /**
     * The value of a refresh token
     **/
    private String refresh_token;

    /**
     * The value of a scope
     **/
    private String scope;

    /**
     * The date of creation of a token
     **/
    private float created_at;

    /**
     * Get the string that contains a OAuth2 token with type
     * @return token with type
     */
    public String getTokenWithType() {
        return token_type + " " + access_token;
    }

    /**
     * Get the value of a OAuth2 token
     * @return value of a OAuth2 token
     */
    public String getAccess_token() {
        return access_token;
    }

    /**
     * Set the value of a OAuth2 token
     * @param access_token value of a OAuth2 token
     */
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    /**
     * Get the value of a token type
     * @return value of a token type
     */
    public String getToken_type() {
        return token_type;
    }

    /**
     * Set the value of a token type
     * @param token_type value of a token type
     */
    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    /**
     * Get the value of a  refresh token
     * @return value of a refresh token
     */
    public String getRefresh_token() {
        return refresh_token;
    }

    /**
     * Set the value of a refresh token
     * @param refresh_token value of a refresh token
     */
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    /**
     * Get the value of a scope
     * @return value of a scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * Set the value of a scope
     * @param scope value of a scope
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Get the date of a token creation
     * @return date of a token creation
     */
    public float getCreated_at() {
        return created_at;
    }

    /**
     * Set the date of a token creation
     * @param created_at date of a token creation
     */
    public void setCreated_at(float created_at) {
        this.created_at = created_at;
    }
}
