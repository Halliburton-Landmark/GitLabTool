package com.ystrazhko.git.services;

import java.util.HashMap;

import com.ystrazhko.git.connections.RESTConnector;

public class LoginServiceImpl implements LoginService {

    private RESTConnector _connector;


    public LoginServiceImpl(RESTConnector connector) {
        setConnector(connector);
    }

    @Override
    public Object login(String name, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("login", name);
        params.put("password", password);
        String json = (String) getConnector().sendPost("/session", params, null, "POST");

        ((GroupsUserService) ServiceProvider.getInstance().getService
                  (GroupsUserServiceImpl.class.getName())).getGroups(json);

        return json;
    }

    private RESTConnector getConnector() {
        return _connector;
    }

    private void setConnector(RESTConnector connector) {
        _connector = connector;
    }

}
