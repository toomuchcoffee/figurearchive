package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.gwt.thirdparty.guava.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@UIScope
@SpringComponent
@Tag("div")
public class ImageSelector extends AbstractCompositeField<VerticalLayout, ImageSelector, Set<Photo>> {
    private final PhotoService photoService;
    private final EventBus.SessionEventBus eventBus;

    private static final Set<Photo> DEFAULT_VALUE = new HashSet<>();

    private String searchTerm = "";

    private ImageGallery availableImages = new ImageGallery(75,5, 2, photo -> {
        Set<Photo> photos = newHashSet(getValue());
        photos.add(photo);
        this.setValue(photos);
    });
    private ImageGallery selectedImages = new ImageGallery(250, 2, 1, photo -> {
        Set<Photo> photos = newHashSet(getValue());
        photos.remove(photo);
        this.setValue(photos);
    });

    public ImageSelector(PhotoService photoService, EventBus.SessionEventBus eventBus) {
        super(DEFAULT_VALUE);
        this.photoService = photoService;
        this.eventBus = eventBus;
    }

    @PostConstruct
    public void init() {
        getContent().add(selectedImages);
        getContent().add(availableImages);

        addValueChangeListener(e -> {
            selectedImages.update(new ArrayList<>(getValue()));
            availableImages.update(availablePhotos());
        });

        eventBus.subscribe(this);
    }

    @EventBusListenerMethod
    public void update(String event) {
        this.searchTerm = event;
        availableImages.update(availablePhotos());
    }

    private List<Photo> availablePhotos() {
        return photoService.findPhotosForVerbatim(searchTerm).stream()
                .filter(this::isSelected)
                .collect(toList());
    }

    private boolean isSelected(Photo photo) {
        return !getValue().stream().map(Photo::getId).collect(toSet()).contains(photo.getId());
    }

    @Override
    protected void setPresentationValue(Set<Photo> s) {
    }

}
