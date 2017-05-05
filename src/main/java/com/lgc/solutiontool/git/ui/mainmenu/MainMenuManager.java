package com.lgc.solutiontool.git.ui.mainmenu;

import com.lgc.solutiontool.git.ui.javafx.controllers.ModularController;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.util.*;

/**
 * Class for managing an main menu with menu items
 *
 * @author Pavlo Pidhorniy
 */
public class MainMenuManager {

    private static MainMenuManager instance = null;

    private List<Menu> items;

    private MainMenuManager() {
    }

    /**
     * Gets instance's the class
     *
     * @return instance
     */
    public static MainMenuManager getInstance() {
        if (instance == null) {
            instance = new MainMenuManager();
        }
        return instance;
    }

    /**
     * Create menu items for main menu
     * Important note: invoke before create a child view in {@link ModularController}
     *
     * @param windowId Id of view where should be created main menu
     * @return List of menus with menu items
     */
    public List<Menu> createToolbarItems(String windowId) {

        items = new ArrayList<>();
        List<Menu> menus = new ArrayList<>();
        LinkedHashSet<String> menusTitles = new LinkedHashSet<>();

        Arrays.stream(MainMenuItems.values())
                .filter(x -> x.getViewKey().equals(windowId))
                .map(MainMenuItems::getMenuName)
                .forEach(menusTitles::add);

        menusTitles.forEach(x -> menus.add(new Menu(x)));

        for (Menu menu : menus) {
            for (MainMenuItems button : MainMenuItems.values()) {
                if (button.getViewKey().equals(windowId) && button.getMenuName().equals(menu.getText())) {
                    menu.getItems().add(createButton(button.getId(), button.getText()));
                }
            }
        }

        items = menus;

        return menus;
    }

    /**
     * Returns menu item from current view
     *
     * @param menuItem enum item (see {@link MainMenuItems}) assigned to this menu item
     * @return Existing menu item with chosen id and name of parent menu or empty menu item if does not matches
     */
    public MenuItem getButtonById(MainMenuItems menuItem) {
        if (items == null) {
            return new MenuItem();
        }

        List<MenuItem> allItems = new ArrayList<>();

        items.forEach(menu1 -> {
            allItems.addAll(menu1.getItems());
        });

        return allItems.stream()
                .filter(x -> x.getParentMenu().getText().equals(menuItem.getMenuName()))
                .filter(x -> x.getId().equals(menuItem.getId()))
                .findFirst()
                .orElseGet(MenuItem::new);

    }

    /**
     * Returns all existing menu items on current view (if menu items has been created)
     *
     * @return all menu items from current view
     */
    public List<Menu> getAllButtonsForCurrentView() {
        if (items == null) {
            return Collections.emptyList();
        }

        return items;
    }

    private MenuItem createButton(String buttonId, String btnText) {
        MenuItem button = new MenuItem(btnText);
        button.setId(buttonId);

        return button;
    }
}
