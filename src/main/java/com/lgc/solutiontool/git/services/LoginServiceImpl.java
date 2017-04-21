package com.lgc.solutiontool.git.services;

import java.util.HashMap;

import com.lgc.solutiontool.git.connections.RESTConnector;
import com.lgc.solutiontool.git.connections.token.PrivateToken;
import com.lgc.solutiontool.git.entities.User;
import com.lgc.solutiontool.git.util.JSONParser;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class LoginServiceImpl implements LoginService {

    private RESTConnector _connector;
    private User _currentUser;

    public LoginServiceImpl(RESTConnector connector) {
        setConnector(connector);
    }

    @Override
    public Object login(String serverURL, String name, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("login", name);
        params.put("password", password);
        getConnector().setUrlMainPart(serverURL);
        Object userJson = getConnector().sendPost("/session", params, null);
        _currentUser = JSONParser.parseToObject(userJson, User.class);
        CredentialsProvider.setDefault(new UsernamePasswordCredentialsProvider(name, password));
        PrivateToken.getInstance().setPrivateTokenValue(_currentUser.getPrivate_token());
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