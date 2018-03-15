package com.lgc.gitlabtool.git.ui.javafx.controllers.listview;

import java.util.List;

/**
 * This interface represent manager for javafx components.
 *
 * Created by Oleksandr Kozlov on 2/19/2018.
 */
public interface ComponentMgr<T> {

    /**
     * This method add component
     *
     * @param component
     */
    public void add(T component);

    /**
     * This method remove component
     *
     * @param component
     */
    public void remove(T component);

    /**
     * This method return component count
     *
     * @return component count
     */
    public int getComponentCount();

    /**
     * This method return components
     *
     * @return list of components
     */
    public List<T> getComponents();

}
