package com.lgc.gitlabtool.git.ui.mainmenu;

import java.util.*;

import com.lgc.gitlabtool.git.services.ServiceProvider;
import com.lgc.gitlabtool.git.services.ThemeService;
import com.lgc.gitlabtool.git.ui.javafx.controllers.ModularController;
import com.lgc.gitlabtool.git.ui.toolbar.GLToolButtons;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

/**
 * Class for managing an main menu with menu items
 *
 * @author Pavlo Pidhorniy
 */
public class MainMenuManager {

    private static MainMenuManager instance = null;

    private List<Menu> items;
    private Map<String, Boolean> enableMap = new HashMap<>();

    private static final ThemeService _themeService = (ThemeService) ServiceProvider.getInstance()
            .getService(ThemeService.class.getName());

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
    public List<Menu> createMainMenuItems(String windowId) {

        items = new ArrayList<>();
        List<Menu> menus = new ArrayList<>();
        LinkedHashSet<String> menusTitles = new LinkedHashSet<>();

        Arrays.stream(GLToolButtons.values())
                .filter(x -> isValidItemForView(windowId, x))
                .map(GLToolButtons::getMainMenuInfo)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(GLToolButtons.MainMenuInfo::getOrder))
                .map(GLToolButtons.MainMenuInfo::getName)
                .forEach(menusTitles::add);

        menusTitles.forEach(x -> menus.add(new Menu(x)));

        for (Menu menu : menus) {
            for (GLToolButtons button : GLToolButtons.values()) {
                if (isValidItemForView(windowId, button) && button.getMainMenuInfo() != null && button.getMainMenuInfo().getName().equals(menu.getText())) {
                	MenuItem menuItem = createButton(button.getId(), button.getIconUrl(), button.getText());
                    if (menuItem != null && !menu.getItems().contains(menuItem)) {
                    	menu.getItems().add(menuItem);
                    }
                }
            }
        }

        items = menus;

        return menus;
    }

    /**
     * Returns menu item from current view
     *
     * @param menuItem enum item (see {@link GLToolButtons}) assigned to this menu item
     * @return Existing menu item with chosen id and name of parent menu or empty menu item if does not matches
     */
    public MenuItem getButtonById(GLToolButtons menuItem) {
        if (items == null) {
            return new MenuItem();
        }

        List<MenuItem> allItems = new ArrayList<>();

        items.forEach(menu1 -> {
            allItems.addAll(menu1.getItems());
        });

        return allItems.stream()
                .filter(x -> x.getParentMenu().getText().equals(menuItem.getMainMenuInfo().getName()))
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

    /**
     * Temporary lock all buttons in mainmenu and makes backup of disable states
     */
    public void lockButtons() {
        if (items == null || enableMap == null) {
            return;
        }

        items.stream()
                .filter(Objects::nonNull)
                .forEach(menu -> {
                    enableMap.put(menu.getId(), menu.isDisable());
                    menu.setDisable(true);
                });
    }

    /**
     * Unlock all buttons in mainmenu after locking and restore backup disable states
     */
    public void unlockButtons() {
        if (items == null || enableMap == null) {
            return;
        }

        items.stream()
                .filter(Objects::nonNull)
                .forEach(menu -> {
                    if (enableMap.containsKey(menu.getId())) {
                        menu.setDisable(enableMap.get(menu.getId()));
                    }
                });
    }

    private MenuItem createButton(String buttonId, String imgPath, String btnText) {
        ImageView view = _themeService.getStyledImageView(imgPath);

        MenuItem menuItem = new MenuItem(btnText, view);
        menuItem.setId(buttonId);

        return menuItem;
    }

    private boolean isValidItemForView(String viewKey, GLToolButtons item) {
        return item.getViewKey().equals(viewKey)
                || item.getViewKey().equals(GLToolButtons.GLToolConstants.ALL_WINDOW_KEY);
    }
}
