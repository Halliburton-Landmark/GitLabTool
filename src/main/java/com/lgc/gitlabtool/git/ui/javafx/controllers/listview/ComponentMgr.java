package com.lgc.gitlabtool.git.ui.javafx.controllers.listview;

import java.util.List;

/**
 * This interface represent manager for javafx components.
 *
 * Created by Oleksandr Kozlov on 2/19/2018.
 */
public interface ComponentMgr<T> {

        public void add(T component);

        public void remove(T component);

        public int getComponentCount();

        public List<T> getComponents();

}
