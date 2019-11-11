package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import de.toomuchcoffee.figurearchive.config.ConfigProperties;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.DataChangedEvent;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoSearchEvent;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import de.toomuchcoffee.figurearchive.util.FigureDisplayNameHelper;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import java.util.Arrays;

import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;
import static java.util.stream.Collectors.toList;

public class PhotoGrid extends Grid<Photo> {
    private static final int PAGE_SIZE = 250;

    private final PhotoService photoService;

    PhotoGrid(
            EventBus.SessionEventBus eventBus,
            PhotoService photoService,
            ConfigProperties properties,
            ValueChangeListener<ValueChangeEvent<Photo>> valueChangeListener) {
        super(Photo.class);
        this.photoService = photoService;
        eventBus.subscribe(this);

        asSingleSelect().addValueChangeListener(valueChangeListener);
        setItems(photoService.findPhotos(0, PAGE_SIZE, null));
        setPageSize(properties.getPhotos().getPageSize());
        setColumns("postId");
        addComponentColumn(photo -> new Image(getImageUrl(photo, 250), "N/A"))
                .setHeader("Image");
        addComponentColumn(photo -> new UnorderedList(photo.getUrls().stream()
                .map(url -> url.getWidth() + "x" + url.getHeight())
                .map(ListItem::new)
                .collect(toList())
                .toArray(new ListItem[0])))
                .setHeader("Available Sizes (WxH)");
        addComponentColumn(photo -> new UnorderedList(Arrays.stream(photo.getTags())
                .map(ListItem::new)
                .collect(toList())
                .toArray(new ListItem[0])))
                .setHeader("Tags");
        addComponentColumn(photo -> new UnorderedList(photo.getFigures().stream()
                .map(FigureDisplayNameHelper::getDisplayName)
                .map(ListItem::new)
                .collect(toList())
                .toArray(new ListItem[0])))
                .setHeader("Figures");
    }

    @EventBusListenerMethod
    public void update(PhotoSearchEvent event) {
        setItems(photoService.findPhotos(event.getPage(), PAGE_SIZE, event.getQuery()));
    }

    @EventBusListenerMethod
    public void update(DataChangedEvent event) {
        setItems(photoService.findPhotos(0, PAGE_SIZE, null));
    }

}
