package com.lgc.gitlabtool.git.connections.token;

import com.google.gson.annotations.SerializedName;

/**
 * This class keeps data about a OAuth2 token.
 *
 * @author Pavlo Pidhorniy
 */
public class Token {

    /**
     * The value of a OAuth2 token
     **/
    @SerializedName("access_token")
    private String accessToken;

    /**
     * The type of a token
     **/
    @SerializedName("token_type")
    private String tokenType;

    /**
     * The value of a refresh token
     **/
    @SerializedName("refresh_token")
    private String refreshToken;

    /**
     * The value of a scope
     **/
    @SerializedName("scope")
    private String scope;

    /**
     * The date of creation of a token
     **/
    @SerializedName("created_at")
    private float createdAt;

    /**
     * Get the string that contains a OAuth2 token with type
     * @return token with type
     */
    public String getTokenWithType() {
        return tokenType + " " + accessToken;
    }

    /**
     * Get the value of a OAuth2 token
     * @return value of a OAuth2 token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Set the value of a OAuth2 token
     * @param accessToken value of a OAuth2 token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Get the value of a token type
     * @return value of a token type
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * Set the value of a token type
     * @param tokenType value of a token type
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * Get the value of a  refresh token
     * @return value of a refresh token
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Set the value of a refresh token
     * @param refreshToken value of a refresh token
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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
    public float getCreatedAt() {
        return createdAt;
    }

    /**
     * Set the date of a token creation
     * @param createdAt date of a token creation
     */
    public void setCreatedAt(float createdAt) {
        this.createdAt = createdAt;
    }
}
