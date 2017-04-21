package com.lgc.solutiontool.git.services;

import com.lgc.solutiontool.git.entities.User;

public interface LoginService {

    Object login(String name, String password);

    User getCurrentUser();
    
    void setServerUrl(String url);
    
    String getServerUrl();

}
