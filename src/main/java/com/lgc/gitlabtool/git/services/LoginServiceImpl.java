package com.lgc.gitlabtool.git.services;

import java.util.HashMap;
import java.util.function.Consumer;

import org.apache.http.HttpStatus;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.lgc.gitlabtool.git.connections.HttpResponseHolder;
import com.lgc.gitlabtool.git.connections.RESTConnector;
import com.lgc.gitlabtool.git.connections.token.CurrentUser;
import com.lgc.gitlabtool.git.connections.token.Token;
import com.lgc.gitlabtool.git.entities.User;
import com.lgc.gitlabtool.git.ui.javafx.dto.DialogDTO;
import com.lgc.gitlabtool.git.util.JSONParser;

public class LoginServiceImpl implements LoginService {

    private RESTConnector _connector;
    private CurrentUser _currentUser;
    private BackgroundService _backgroundService;

    public LoginServiceImpl(RESTConnector connector, BackgroundService backgroundService) {
        setConnector(connector);
        this._backgroundService = backgroundService;
    }

    @Override
    public void login(DialogDTO dto, Consumer<HttpResponseHolder> onSuccess) {
        Runnable runnable = () -> {
            HashMap<String, String> params = new HashMap<>();
            params.put("grant_type", "password");
            params.put("username", dto.getLogin());
            params.put("password", dto.getPassword());

            getConnector().setUrlMainPart(dto.getShortServerURL());
            HttpResponseHolder responseHolder = getConnector().sendPost("/oauth/token", params, null);
            Object tokenJson = responseHolder != null ? responseHolder.getBody() : null;
            if (isResponseCodeValid(responseHolder, tokenJson)) {
                Token accessToken = JSONParser.parseToObject(tokenJson, Token.class);
                if (accessToken != null) {
                    HashMap<String, String> secureHeader = new HashMap<>();
                    secureHeader.put("Authorization", accessToken.getTokenWithType());
                    getConnector().setUrlMainPart(dto.getServerURL());
                    HttpResponseHolder userInfoResponseHolder = getConnector().sendGet("/user", null, secureHeader);
                    Object userJson2 = userInfoResponseHolder != null ? userInfoResponseHolder.getBody() : null;
                    if (isResponseCodeValid(userInfoResponseHolder, userJson2)) {
                        User newUser = JSONParser.parseToObject(userJson2, User.class);
                        _currentUser = CurrentUser.getInstance();
                        _currentUser.setCurrentUser(newUser);
                        _currentUser.setPrivateTokenValue(accessToken.getTokenWithType());
                    }

                    CredentialsProvider.setDefault(new UsernamePasswordCredentialsProvider(dto.getLogin(), dto.getPassword()));
                }
            }
            onSuccess.accept(responseHolder);
        };
        _backgroundService.runInBackgroundThread(runnable);
    }

    @Override
    public User getCurrentUser() {
        return _currentUser.getCurrentUser();
    }
    
    private boolean isResponseCodeValid(HttpResponseHolder responseHolder, Object userJson) {
        return !(responseHolder.getResponseCode() == HttpStatus.SC_REQUEST_TIMEOUT
                || responseHolder.getResponseCode() == 0 || userJson == null);
    }

    private RESTConnector getConnector() {
        return _connector;
    }

    private void setConnector(RESTConnector connector) {
        _connector = connector;
    }

    @Override
    public String getServerURL() {
        return getConnector().getUrlMainPart();
    }

}