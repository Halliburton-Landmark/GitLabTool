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

    public LoginServiceImpl(RESTConnector connector) {
        setConnector(connector);
    }

    @Override
    public void login(DialogDTO dto, Consumer<Integer> onSuccess) {
        Runnable runnable = () -> {
            HashMap<String, String> params = new HashMap<>();
            params.put("login", dto.getLogin());
            params.put("password", dto.getPassword());
            getConnector().setUrlMainPart(dto.getServerURL());
            HttpResponseHolder responseHolder = getConnector().sendPost("/session", params, null);
            Object userJson = responseHolder != null ? responseHolder.getBody() : null;
            if (userJson == null) {
                onSuccess.accept(HttpStatus.SC_UNAUTHORIZED);
            } else {
                _currentUser = CurrentUser.getInstance();
                _currentUser.setCurrentUser(JSONParser.parseToObject(userJson, User.class));
                CredentialsProvider.setDefault(new UsernamePasswordCredentialsProvider(dto.getLogin(), dto.getPassword()));

                onSuccess.accept(HttpStatus.SC_OK);
            }
        };
        Thread loginThread = new Thread(runnable);
        loginThread.setName("LoginThread");
        loginThread.start();
    }

    @Override
    public User getCurrentUser() {
        return _currentUser.getCurrentUser();
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