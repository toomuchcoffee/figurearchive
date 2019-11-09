package de.toomuchcoffee.figurearchive.view;

import com.google.common.collect.ImmutableMap;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.router.Route;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService;
import de.toomuchcoffee.figurearchive.service.TumblrPostService;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;

import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;
import static com.vaadin.flow.component.icon.VaadinIcon.REFRESH;

@Route
@RequiredArgsConstructor
public class MainView extends VerticalLayout {

    private final ConfigurableFilterDataProvider<Figure, Void, FigureService.FigureFilter> figureDataProvider;
    private final FigureFilterPanel figureFilterPanel;
    private final FigureEditor figureEditor;
    private final PhotoEditor photoEditor;
    private final CsvUploader csvUploader;
    private final LogoutButton logoutButton;
    private final TumblrPostService tumblrPostService;

    @PostConstruct
    public void init() {
        FigureGrid figureGrid = new FigureGrid(
                figureDataProvider, e -> Optional.ofNullable(e.getValue()).ifPresent(figureEditor::editFigure));

        Button createButton = new Button("New figure", PLUS.create(), e -> figureEditor.createFigure());

        Button refreshButton = new Button("Refresh Tumblr", REFRESH.create(), e -> tumblrPostService.loadPosts());

        HorizontalLayout actions = new HorizontalLayout(createButton, csvUploader, refreshButton, logoutButton);
        add(actions);

        VerticalLayout tabSheet = new VerticalLayout(figureGrid);

        Map<Tab, Component> tabsToPages = ImmutableMap.of(
                new Tab("Figures"), figureGrid,
                new Tab("Photos"), photoEditor);
        Tabs tabs = new Tabs(tabsToPages.keySet().toArray(new Tab[0]));

        tabs.addSelectedChangeListener(event -> {
            tabSheet.removeAll();
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            tabSheet.add(selectedPage);
        });

        add(tabs);

        add(figureFilterPanel, tabSheet);
    }

}
