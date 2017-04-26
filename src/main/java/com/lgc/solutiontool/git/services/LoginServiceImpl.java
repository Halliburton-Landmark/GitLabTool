package com.lgc.solutiontool.git.services;

import java.util.HashMap;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.lgc.solutiontool.git.connections.RESTConnector;
import com.lgc.solutiontool.git.connections.token.CurrentUser;
import com.lgc.solutiontool.git.entities.User;
import com.lgc.solutiontool.git.util.JSONParser;

public class LoginServiceImpl implements LoginService {

    private RESTConnector _connector;
    private CurrentUser _currentUser;

    public LoginServiceImpl(RESTConnector connector) {
        setConnector(connector);
    }

    @Override
    public Object login(String name, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("login", name);
        params.put("password", password);
        Object userJson = getConnector().sendPost("/session", params, null);

        _currentUser = CurrentUser.getInstance();
        _currentUser.setCurrentUser(JSONParser.parseToObject(userJson, User.class));
        CredentialsProvider.setDefault(new UsernamePasswordCredentialsProvider(name, password));
        return _currentUser.getCurrentUser();
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

}