package com.lgc.gitlabtool.git.services;

import java.util.HashMap;
import java.util.function.Consumer;

import org.apache.http.HttpStatus;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.lgc.gitlabtool.git.connections.HttpResponseHolder;
import com.lgc.gitlabtool.git.connections.RESTConnector;
import com.lgc.gitlabtool.git.connections.token.CurrentUser;
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
            params.put("login", dto.getLogin());
            params.put("password", dto.getPassword());
            getConnector().setUrlMainPart(dto.getServerURL());
            HttpResponseHolder responseHolder = getConnector().sendPost("/session", params, null);
            Object userJson = responseHolder != null ? responseHolder.getBody() : null;
            if (isResponseCodeValid(responseHolder, userJson)) {
                _currentUser = CurrentUser.getInstance();
                _currentUser.setCurrentUser(JSONParser.parseToObject(userJson, User.class));
                CredentialsProvider.setDefault(new UsernamePasswordCredentialsProvider(dto.getLogin(), dto.getPassword()));
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
        if (responseHolder.getResponseCode() == HttpStatus.SC_REQUEST_TIMEOUT
                || responseHolder.getResponseCode() == 0 || userJson == null) {
            return false;
        }
        return true;
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