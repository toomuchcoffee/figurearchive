package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.ConfigProperties;
import de.toomuchcoffee.figurearchive.service.FigureService;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class FigurePanel extends VerticalLayout {

    private final ConfigProperties properties;
    private final FigureActionsPanel figureActionsPanel;
    private final FigureEditor figureEditor;
    private final FigureService figureService;
    private final EventBus.SessionEventBus eventBus;

    @PostConstruct
    public void init() {
        FigureGrid figureGrid = new FigureGrid(eventBus, figureService, properties, figureEditor);

        add(figureActionsPanel, figureGrid);
    }

}
