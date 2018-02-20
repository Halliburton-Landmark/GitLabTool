package com.lgc.gitlabtool.git.ui.javafx.controllers.listview;

import javafx.scene.control.Control;

import java.util.List;

/**
 * Created by Oleksandr Kozlov on 2/19/2018.
 */
public interface ComponentMgr<T> {

        public void add(T component);

        public void remove(T component);

        public int getComponentCount();

        public List<T> getComponents();

}
