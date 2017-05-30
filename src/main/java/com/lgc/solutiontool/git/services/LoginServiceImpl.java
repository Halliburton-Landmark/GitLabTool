package com.lgc.solutiontool.git.services;

import java.util.HashMap;
import java.util.function.Consumer;

import org.apache.http.HttpStatus;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.lgc.solutiontool.git.connections.RESTConnector;
import com.lgc.solutiontool.git.connections.token.CurrentUser;
import com.lgc.solutiontool.git.entities.User;
import com.lgc.solutiontool.git.ui.javafx.dto.DialogDTO;
import com.lgc.solutiontool.git.util.JSONParser;

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
            Object userJson = getConnector().sendPost("/session", params, null);
            if (userJson == null) {
                onSuccess.accept(HttpStatus.SC_UNAUTHORIZED);
            } else {
                onSuccess.accept(HttpStatus.SC_OK);
            }

            _currentUser = CurrentUser.getInstance();
            _currentUser.setCurrentUser(JSONParser.parseToObject(userJson, User.class));
            CredentialsProvider.setDefault(new UsernamePasswordCredentialsProvider(dto.getLogin(), dto.getPassword()));
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