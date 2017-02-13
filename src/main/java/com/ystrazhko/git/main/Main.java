package com.ystrazhko.git.main;

import com.ystrazhko.git.ui.UserInterface;
import com.ystrazhko.git.ui.javafx.JavaFXUI;

public class Main {
    public static void main(String[] args) {
        final UserInterface ui = new JavaFXUI();
        ui.run(args);
    }
}
