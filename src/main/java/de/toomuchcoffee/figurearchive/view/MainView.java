package de.toomuchcoffee.figurearchive.view;

import com.google.common.collect.ImmutableMap;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.router.Route;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;

@Route
@RequiredArgsConstructor
public class MainView extends VerticalLayout {

    private final ConfigurableFilterDataProvider<Figure, Void, FigureService.FigureFilter> figureDataProvider;
    private final FigureActionsPanel figureActionsPanel;
    private final FigureEditor figureEditor;
    private final PhotoEditor photoEditor;
    private final LogoutButton logoutButton;
    private final TumblrSyncButton tumblrSyncButton;

    @PostConstruct
    public void init() {
        FigureGrid figureGrid = new FigureGrid(
                figureDataProvider, e -> Optional.ofNullable(e.getValue()).ifPresent(figureEditor::editFigure));

        Map<Tab, Component> tabsToPages = ImmutableMap.of(
                new Tab("Figures"), figureGrid,
                new Tab("Photos"), photoEditor);

        VerticalLayout tabSheet = new VerticalLayout();
        TabMenu tabMenu = new TabMenu(tabsToPages, tabSheet, 0);

        HorizontalLayout actions = new HorizontalLayout(tabMenu, tumblrSyncButton, logoutButton);
        add(actions);

        add(figureActionsPanel, tabSheet);
    }

}
