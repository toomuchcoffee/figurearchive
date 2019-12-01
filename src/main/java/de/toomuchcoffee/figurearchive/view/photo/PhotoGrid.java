package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.ValueProvider;
import de.toomuchcoffee.figurearchive.config.ConfigProperties;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.event.PhotoChangedEvent;
import de.toomuchcoffee.figurearchive.event.PhotoSearchEvent;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;
import static java.util.stream.Collectors.toList;

public class PhotoGrid extends Grid<Photo> {
    private static final int PAGE_SIZE = 250;

    private final PhotoService photoService;

    PhotoGrid(
            EventBus.SessionEventBus eventBus,
            PhotoService photoService,
            ConfigProperties properties,
            PhotoGridEditor photoGridEditor) {
        super(Photo.class);
        this.photoService = photoService;

        addItemClickListener(e -> photoGridEditor.editPhoto(e.getItem()));
        setPageSize(properties.getPhotos().getPageSize());
        setColumns("postId");

        addComponentColumn(photo -> {
            Checkbox checkbox = new Checkbox();
            checkbox.setValue(photo.isCompleted());
            checkbox.setEnabled(false);
            return checkbox;
        })
                .setAutoWidth(true)
                .setHeader("Completed?");
        addComponentColumn(photo -> new Image(getImageUrl(photo, 75), "N/A"))
                .setWidth("75px")
                .setHeader("Image");
        addColumn((ValueProvider<Photo, Integer>) photo -> photo.getFigures().size()).setHeader("Nr. of Figures");

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

        switch (event.getOperation()) {
            case UPDATED: {
                if (event.getValue().isCompleted()) {
                    items.remove(indexOf);
                    items.add(event.getValue());
                } else {
                    items.set(indexOf, event.getValue());
                }
                break;
            }
            case DELETED: {
                items.remove(indexOf);
                break;
            }
            case CREATED:
            default:
                break;
        }
        setItems(items);
        getDataProvider().refreshAll();
    }

    @SuppressWarnings("unchecked")
    private Collection<Photo> getItems() {
        return getDataProvider() == null ? new ArrayList<>() : ((ListDataProvider<Photo>) getDataProvider()).getItems();
    }

}
