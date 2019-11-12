package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import de.toomuchcoffee.figurearchive.config.ConfigProperties;
import de.toomuchcoffee.figurearchive.config.EventBusConfig;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.FigureSearchEvent;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;
import static java.util.Comparator.*;

public class FigureGrid extends Grid<Figure> {

    private final FigureService figureService;

    public FigureGrid(
            EventBus.SessionEventBus eventBus,
            FigureService figureService,
            ConfigProperties properties,
            ValueChangeListener<ValueChangeEvent<Figure>> valueChangeListener) {
        super(Figure.class);
        this.figureService = figureService;
        eventBus.subscribe(this);

        asSingleSelect().addValueChangeListener(valueChangeListener);
        setItems(figureService.findFigures(null));
        setPageSize(properties.getFigures().getPageSize());
        setColumns("placementNo", "verbatim", "productLine", "year");
        getColumnByKey("placementNo").setWidth("150px").setFlexGrow(0);
        addComponentColumn(f -> f.getPhotos().isEmpty() ? new Span() : new Image(getImageUrl(f.getPhotos().iterator().next(), 75), "n/a"))
                .setComparator(comparing(figure -> figure.getPhotos().size(), nullsFirst(naturalOrder())))
                .setHeader("Image");
    }

    @EventBusListenerMethod
    public void update(FigureSearchEvent event) {
        setItems(figureService.findFigures(event.getValue()));
    }

    @EventBusListenerMethod
    public void update(EventBusConfig.DataChangedEvent event) {
        setItems(figureService.findFigures(null));
    }


}
