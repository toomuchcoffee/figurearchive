package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.toomuchcoffee.figurearchive.config.ConfigProperties;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.event.PhotoChangedEvent;
import de.toomuchcoffee.figurearchive.event.PhotoSearchEvent;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import de.toomuchcoffee.figurearchive.util.FigureDisplayNameHelper;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;
import static java.util.Comparator.*;
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

        asSingleSelect().addValueChangeListener(valueChangeListener);
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
                .setHeader("Figures")
                .setComparator(comparing(photo -> photo.getFigures().size(), nullsFirst(naturalOrder())));

        addAttachListener(e -> eventBus.subscribe(this));
        addDetachListener(e -> eventBus.unsubscribe(this));
    }

    @EventBusListenerMethod
    public void update(PhotoSearchEvent event) {
        setItems(photoService.findPhotosByTag(event.getPage(), PAGE_SIZE, event.getFilter()));
    }

    @EventBusListenerMethod
    public void update(PhotoChangedEvent event) {
        ArrayList<Photo> items = new ArrayList<>(getItems());
        List<Long> ids = items.stream().map(Photo::getId).collect(toList());
        Long id = event.getValue().getId();
        int indexOf = ids.indexOf(id);
        photoService.findById(id).ifPresent(photo -> {
            items.set(indexOf, photo);
        });
        setItems(items);
    }

    @SuppressWarnings("unchecked")
    private Collection<Photo> getItems() {
        return getDataProvider() == null ? new ArrayList<>() : ((ListDataProvider<Photo>) getDataProvider()).getItems();
    }

}
