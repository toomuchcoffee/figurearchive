package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.event.PhotoSearchByVerbatimEvent;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static de.toomuchcoffee.figurearchive.util.ValueSetHelper.add;
import static de.toomuchcoffee.figurearchive.util.ValueSetHelper.remove;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;

@UIScope
@SpringComponent
@Tag("div")
public class PhotoSelector extends AbstractCompositeField<VerticalLayout, PhotoSelector, Set<Photo>> {
    private final PhotoService photoService;
    private final EventBus.SessionEventBus eventBus;

    private static final Set<Photo> DEFAULT_VALUE = new HashSet<>();

    private List<Photo> foundPhotos = newArrayList();

    private PhotoGallery availableImages;
    private PhotoGallery selectedImages;

    public PhotoSelector(PhotoService photoService, EventBus.SessionEventBus eventBus) {
        super(DEFAULT_VALUE);
        this.photoService = photoService;
        this.eventBus = eventBus;
    }

    @PostConstruct
    public void init() {
        selectedImages = new PhotoGallery(250, 2, photo -> remove(this, photo));
        getContent().add(selectedImages);

        availableImages = new PhotoGallery(75, 5, photo -> add(this, photo));
        getContent().add(availableImages);

        addValueChangeListener(e -> {
            selectedImages.update(new ArrayList<>(getValue()));
            availableImages.update(availablePhotos());
        });

        eventBus.subscribe(this);
    }

    @EventBusListenerMethod
    public void update(PhotoSearchByVerbatimEvent event) {
        if (isBlank(event.getFilter())) {
            foundPhotos = newArrayList();
        } else {
            foundPhotos = photoService.suggestPhotos(event.getFilter());
        }
        availableImages.update(availablePhotos());
    }

    private List<Photo> availablePhotos() {
        return foundPhotos.stream()
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
