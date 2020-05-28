package de.toomuchcoffee.figurearchive.view.main;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.ConfigProperties;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.event.FigureSearchEvent;
import de.toomuchcoffee.figurearchive.event.FigureSearchResultEvent;
import de.toomuchcoffee.figurearchive.service.FigureService;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;
import java.util.Set;

import static com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER;

@UIScope
@SpringComponent
public class PublicFigureGrid extends Grid<Figure> {
    private static final int PAGE_SIZE = 100;

    private final FigureService figureService;
    private final PhotoService photoService;
    private final EventBus.UIEventBus eventBus;
    private final ConfigProperties properties;

    public PublicFigureGrid(FigureService figureService, PhotoService photoService, EventBus.UIEventBus eventBus, ConfigProperties properties) {
        super(Figure.class);
        this.figureService = figureService;
        this.photoService = photoService;
        this.eventBus = eventBus;
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        addItemClickListener(e -> {
            Figure figure = e.getItem();
            Set<Photo> photos = photoService.findByFigure(figure);
            new PublicFigureViewer(figure, photos).open();
        });

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
        Notification.show(event.getCount() + " figures found!", 5000, TOP_CENTER);
    }

}
