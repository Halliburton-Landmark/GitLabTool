package com.lgc.solutiontool.git.services;

import java.util.function.Consumer;

import com.lgc.solutiontool.git.entities.User;
import com.lgc.solutiontool.git.ui.javafx.dto.DialogDTO;

public interface LoginService {

    Object login(DialogDTO dialogDTO, Consumer<Integer> handler);

    User getCurrentUser();

    String getServerURL();

}
