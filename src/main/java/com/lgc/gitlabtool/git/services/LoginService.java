package com.lgc.gitlabtool.git.services;

import java.util.function.Consumer;

import com.lgc.gitlabtool.git.ui.javafx.dto.DialogDTO;
import com.lgc.gitlabtool.git.entities.User;

public interface LoginService {

    void login(DialogDTO dialogDTO, Consumer<Integer> onSuccess);

    User getCurrentUser();

    String getServerURL();

}
