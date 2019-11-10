package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

import static de.toomuchcoffee.figurearchive.util.ValueSetHelper.add;
import static de.toomuchcoffee.figurearchive.util.ValueSetHelper.remove;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@UIScope
@SpringComponent
@Tag("div")
public class FigureSelector extends AbstractCompositeField<HorizontalLayout, FigureSelector, Set<Figure>> {
    private final FigureService figureService;
    private final EventBus.SessionEventBus eventBus;

    private static final Set<Figure> DEFAULT_VALUE = new HashSet<>();

    private String searchTerm = "";

    private FigureList availableFigures = new FigureList();
    private FigureList selectedFigures = new FigureList();

    public FigureSelector(FigureService figureService, EventBus.SessionEventBus eventBus) {
        super(DEFAULT_VALUE);
        this.figureService = figureService;
        this.eventBus = eventBus;
    }

    @PostConstruct
    public void init() {
        getContent().add(new VerticalLayout(new Label("Selected Figures"), selectedFigures));
        getContent().add(new VerticalLayout(new Label("Available Figures"), availableFigures));

        availableFigures.asSingleSelect().addValueChangeListener(e -> add(this, e.getValue()));

        selectedFigures.asSingleSelect().addValueChangeListener(e -> remove(this, e.getValue()));

        addValueChangeListener(e -> {
            selectedFigures.update(new ArrayList<>(getValue()));
            availableFigures.update(availableFigures());
        });

        eventBus.subscribe(this);
    }

    @EventBusListenerMethod
    public void update(PhotoSearchEvent event) {
        this.searchTerm = event.getValue();
        availableFigures.update(availableFigures());
    }

    private List<Figure> availableFigures() {
        return figureService.findFigures(searchTerm).stream()
                .filter(this::isSelected)
                .collect(toList());
    }

    private boolean isSelected(Figure figure) {
        return !getValue().stream().map(Figure::getId).collect(toSet()).contains(figure.getId());
    }

    @Override
    protected void setPresentationValue(Set<Figure> s) {
    }

}
