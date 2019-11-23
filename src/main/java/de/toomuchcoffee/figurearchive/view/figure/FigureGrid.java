package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.toomuchcoffee.figurearchive.config.ConfigProperties;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.event.FigureChangedEvent;
import de.toomuchcoffee.figurearchive.event.FigureImportEvent;
import de.toomuchcoffee.figurearchive.event.FigureSearchEvent;
import de.toomuchcoffee.figurearchive.service.FigureService;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;
import static java.util.Comparator.*;

public class FigureGrid extends Grid<Figure> {
    private static final int PAGE_SIZE = 100;

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
        setItems(figureService.findFigures(0, PAGE_SIZE, null));
        setPageSize(properties.getFigures().getPageSize());
        setColumns("placementNo", "verbatim", "productLine", "year");
        getColumnByKey("placementNo").setWidth("150px").setFlexGrow(0);
        addComponentColumn(f -> f.getPhotos().isEmpty() ? new Span() : new Image(getImageUrl(f.getPhotos().iterator().next(), 75), "n/a"))
                .setComparator(comparing(figure -> figure.getPhotos().size(), nullsFirst(naturalOrder())))
                .setHeader("Image");
    }

    @EventBusListenerMethod
    public void update(FigureSearchEvent event) {
        setItems(figureService.findFigures(event.getPage(), PAGE_SIZE, event.getFilter()));
    }

    @EventBusListenerMethod
    public void update(FigureChangedEvent event) {
        switch (event.getOperation()) {
            case UPDATED:
                getDataProvider().refreshItem(event.getValue());
                break;
            case CREATED: {
                List<Figure> items = new ArrayList<>(getItems());
                items.add(event.getValue());
                setItems(items);
                getDataProvider().refreshAll();
            }
            ;
            break;
            case DELETED: {
                List<Figure> items = new ArrayList<>(getItems());
                items.remove(event.getValue());
                setItems(items);
                getDataProvider().refreshAll();
            }
            ;
            break;
            default:
                getDataProvider().refreshAll();
        }
    }

    @EventBusListenerMethod
    public void update(FigureImportEvent event) {
        List<Figure> items = new ArrayList<>(getItems());
        items.addAll(event.getValue());
        setItems(items);
        getDataProvider().refreshAll();
    }

    @SuppressWarnings("unchecked")
    private Collection<Figure> getItems() {
        return ((ListDataProvider<Figure>) getDataProvider()).getItems();
    }


}
