package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.toomuchcoffee.figurearchive.service.TumblrPostService;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;
import static com.vaadin.flow.component.icon.VaadinIcon.REFRESH;

@Route
@RequiredArgsConstructor
public class MainView extends VerticalLayout {

    private final FigureFilterPanel figureFilterPanel;
    private final FigureGrid figureGrid;
    private final FigureEditor figureEditor;
    private final CsvUploader csvUploader;
    private final LogoutButton logoutButton;
    private final TumblrPostService tumblrPostService;

    @PostConstruct
    public void init() {
        Button createButton = new Button("New figure", PLUS.create(), e -> figureEditor.createFigure());

        Button refreshButton = new Button("Refresh Tumblr", REFRESH.create(), e -> tumblrPostService.loadPosts());

        HorizontalLayout actions = new HorizontalLayout(createButton, csvUploader, refreshButton, logoutButton);
        add(actions);

        add(figureFilterPanel, figureGrid);
    }

}
