package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;

@Route
@RequiredArgsConstructor
public class MainView extends VerticalLayout {

    private final FigureFilterPanel figureFilterPanel;
    private final FigureGrid figureGrid;
    private final FigureEditor figureEditor;
    private final CsvUploader csvUploader;
    private final LogoutButton logoutButton;

    @PostConstruct
    public void init() {
        Button createButton = new Button("New figure", PLUS.create());
        createButton.addClickListener(e -> figureEditor.createFigure());

        HorizontalLayout actions = new HorizontalLayout(createButton, csvUploader, logoutButton);
        add(actions);

        add(figureFilterPanel, figureGrid);
    }

}
