package com.ystrazhko.git.services;

import java.util.HashMap;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.ystrazhko.git.connections.RESTConnector;
import com.ystrazhko.git.entities.User;
import com.ystrazhko.git.util.JSONParser;

public class LoginServiceImpl implements LoginService {

    private RESTConnector _connector;
    private User _currentUser;

    public LoginServiceImpl(RESTConnector connector) {
        setConnector(connector);
    }

    @Override
    public Object login(String name, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("login", name);
        params.put("password", password);
        Object userJson = getConnector().sendPost("/session", params, null);
        _currentUser = JSONParser.parseToObject(userJson, User.class);
        CredentialsProvider.setDefault(new UsernamePasswordCredentialsProvider(name, password));
        return _currentUser;
    }

    @Override
    public User getCurrentUser() {
        return _currentUser;
    }

    private RESTConnector getConnector() {
        return _connector;
    }

    private void setConnector(RESTConnector connector) {
        _connector = connector;
    }

}
