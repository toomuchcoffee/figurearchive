package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.toomuchcoffee.figurearchive.config.ConfigProperties;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.event.FigureChangedEvent;
import de.toomuchcoffee.figurearchive.event.FigureImportEvent;
import de.toomuchcoffee.figurearchive.event.FigureSearchEvent;
import de.toomuchcoffee.figurearchive.event.FigureSearchResultEvent;
import de.toomuchcoffee.figurearchive.service.FigureService;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER;

public class FigureGrid extends Grid<Figure> {
    private static final int PAGE_SIZE = 100;

    private final FigureService figureService;

    public FigureGrid(
            EventBus.UIEventBus eventBus,
            FigureService figureService,
            ConfigProperties properties,
            FigureEditor figureEditor) {
        super(Figure.class);
        this.figureService = figureService;
        addItemClickListener(e -> figureEditor.editFigure(e.getItem()));

        setPageSize(properties.getFigures().getPageSize());
        setColumns("placementNo", "verbatim", "productLine", "year", "count");
        getColumns().forEach(column -> column.setAutoWidth(true));

        addAttachListener(e -> eventBus.subscribe(this));
        addDetachListener(e -> eventBus.unsubscribe(this));
    }

    @EventBusListenerMethod
    public void update(FigureSearchEvent event) {
        setItems(figureService.findFigures(event.getPage(), PAGE_SIZE, event.getFilter()));
    }

    @EventBusListenerMethod
    public void update(FigureSearchResultEvent event) {
        Notification.show(event.getCount() + " figures found!, (owned: " + event.getOwned() +")", 5000, TOP_CENTER);
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
                break;
            }
            case DELETED: {
                List<Figure> items = new ArrayList<>(getItems());
                items.remove(event.getValue());
                setItems(items);
                getDataProvider().refreshAll();
                break;
            }
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
