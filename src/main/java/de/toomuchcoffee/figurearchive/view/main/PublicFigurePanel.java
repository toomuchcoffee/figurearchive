package de.toomuchcoffee.figurearchive.view.main;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class PublicFigurePanel extends VerticalLayout {

    private final PublicFigureActionsPanel publicFigureActionsPanel;
    private final PublicFigureGrid publicFigureGrid;

    @PostConstruct
    public void init() {
        add(publicFigureActionsPanel, publicFigureGrid);
    }

}
