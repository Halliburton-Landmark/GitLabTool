package com.ystrazhko.git.services;

import com.ystrazhko.git.entities.User;

public interface LoginService {

    Object login(String name, String password);

    User getCurrentUser();

}
