package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.Optional;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class FiguresPanel extends VerticalLayout {

    private final ConfigurableFilterDataProvider<Figure, Void, FigureService.FigureFilter> figureDataProvider;
    private final FigureActionsPanel figureActionsPanel;
    private final FigureEditor figureEditor;

    @PostConstruct
    public void init() {
        FigureGrid figureGrid = new FigureGrid(
                figureDataProvider, e -> Optional.ofNullable(e.getValue()).ifPresent(figureEditor::editFigure));

        add(figureActionsPanel, figureGrid);
    }

}
