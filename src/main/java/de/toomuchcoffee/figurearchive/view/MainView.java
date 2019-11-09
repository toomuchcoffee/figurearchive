package de.toomuchcoffee.figurearchive.view;

import com.google.common.collect.ImmutableMap;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.Route;
import de.toomuchcoffee.figurearchive.view.controls.LogoutButton;
import de.toomuchcoffee.figurearchive.view.controls.TabMenu;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.Map;

@Route
@RequiredArgsConstructor
public class MainView extends VerticalLayout {

    private final FigurePanel figurePanel;
    private final PhotoPanel photoPanel;
    private final LogoutButton logoutButton;

    @PostConstruct
    public void init() {
        Map<Tab, Component> tabsToPages = ImmutableMap.of(
                new Tab("Figures"), figurePanel,
                new Tab("Photos"), photoPanel);

        VerticalLayout tabSheet = new VerticalLayout();
        TabMenu tabMenu = new TabMenu(tabsToPages, tabSheet, 0);

        HorizontalLayout actions = new HorizontalLayout(tabMenu, logoutButton);

        add(actions, tabSheet);
    }

}
