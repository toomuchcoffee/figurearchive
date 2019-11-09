package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoSearchEvent;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@UIScope
@SpringComponent
@Tag("div")
public class FigureSelector extends AbstractCompositeField<VerticalLayout, FigureSelector, Set<Figure>> {
    private final FigureService figureService;
    private final EventBus.SessionEventBus eventBus;

    private static final Set<Figure> DEFAULT_VALUE = new HashSet<>();

    private String searchTerm = "";

    private Grid<Figure> availableFigures = new Grid<Figure>();
    private Grid<Figure> selectedFigures = new Grid<Figure>();

    public FigureSelector(FigureService figureService, EventBus.SessionEventBus eventBus) {
        super(DEFAULT_VALUE);
        this.figureService = figureService;
        this.eventBus = eventBus;
    }

    @PostConstruct
    public void init() {
        getContent().add(new Label("Selected Figures"));
        getContent().add(selectedFigures);
        getContent().add(new Label("Available Figures"));
        getContent().add(availableFigures);

        addValueChangeListener(e -> {
            selectedFigures.setItems(new ArrayList<>(getValue()));
            availableFigures.setItems(availableFigures());
        });

        eventBus.subscribe(this);
    }

    @EventBusListenerMethod
    public void update(PhotoSearchEvent event) {
        this.searchTerm = event.getValue();
        availableFigures.setItems(availableFigures());
    }

    private List<Figure> availableFigures() {
        return figureService.findFigures(searchTerm).stream()
                .filter(this::isSelected)
                .collect(toList());
    }

    private boolean isSelected(Figure photo) {
        return !getValue().stream().map(Figure::getId).collect(toSet()).contains(photo.getId());
    }

    @Override
    protected void setPresentationValue(Set<Figure> s) {
    }

}
