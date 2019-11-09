package de.toomuchcoffee.figurearchive.view;

import com.google.common.collect.ImmutableMap;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.Route;
import de.toomuchcoffee.figurearchive.view.controls.LogoutButton;
import de.toomuchcoffee.figurearchive.view.controls.TabMenu;
import de.toomuchcoffee.figurearchive.view.controls.TumblrSyncButton;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.Map;

@Route
@RequiredArgsConstructor
public class MainView extends VerticalLayout {

    private final FiguresPanel figuresPanel;
    private final PhotoEditor photoEditor;
    private final LogoutButton logoutButton;
    private final TumblrSyncButton tumblrSyncButton;

    @PostConstruct
    public void init() {
        Map<Tab, Component> tabsToPages = ImmutableMap.of(
                new Tab("Figures"), figuresPanel,
                new Tab("Photos"), photoEditor);

        VerticalLayout tabSheet = new VerticalLayout();
        TabMenu tabMenu = new TabMenu(tabsToPages, tabSheet, 0);

        HorizontalLayout actions = new HorizontalLayout(tabMenu, tumblrSyncButton, logoutButton);

        add(actions, tabSheet);
    }

}
