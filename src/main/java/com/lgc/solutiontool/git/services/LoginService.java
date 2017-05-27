package com.lgc.solutiontool.git.services;

import java.util.function.Consumer;

import com.lgc.solutiontool.git.entities.User;
import com.lgc.solutiontool.git.ui.javafx.dto.DialogDTO;

public interface LoginService {

    void login(DialogDTO dialogDTO, Consumer<Integer> onSuccess);

    User getCurrentUser();

    String getServerURL();

}
