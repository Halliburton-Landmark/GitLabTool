package com.lgc.solutiontool.git.connections.Token;

public class PrivateToken {
    private static final String PRIVATE_TOKEN_KEY = "PRIVATE-TOKEN";
    private static PrivateToken _instance;
    private String privateTokenValue;

    private PrivateToken() {

    }

    public static PrivateToken getInstance() {
        if (_instance == null) {
            _instance = new PrivateToken();
        }

        return _instance;
    }

    public String getPrivateTokenKey() {
        return PRIVATE_TOKEN_KEY;
    }

    public String getPrivateTokenValue() {
        return privateTokenValue;
    }

    public void setPrivateTokenValue(String privateTokenValue) {
        this.privateTokenValue = privateTokenValue;
    }
}
