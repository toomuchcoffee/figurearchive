package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.event.FigureChangedEvent;
import de.toomuchcoffee.figurearchive.event.PhotoChangedEvent;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import de.toomuchcoffee.figurearchive.util.ValueSetHelper;
import de.toomuchcoffee.figurearchive.view.figure.FigureEditor;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.vaadin.flow.component.icon.VaadinIcon.*;
import static de.toomuchcoffee.figurearchive.event.EntityChangedEvent.Operation.CREATED;
import static de.toomuchcoffee.figurearchive.event.EntityChangedEvent.Operation.UPDATED;
import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;

@SpringComponent
@UIScope
@RequiredArgsConstructor
public class PhotoEditor extends HorizontalLayout {

    private final PhotoService photoService;
    private final FigureSelector figureSelector;
    private final EventBus.SessionEventBus eventBus;
    private final FigureEditor figureEditor;

    private Photo photo;

    private Binder<Photo> binder;

    private VerticalLayout details;

    @PostConstruct
    public void init() {
        add("No non completed Photos. Nothing to do...");
        photoService.anyNotCompleted().ifPresent(this::nextPhoto);
        eventBus.subscribe(this);
    }

    private void nextPhoto(Photo photo) {
        removeAll();

        this.photo = photo;
        binder = new Binder<>();
        binder.setBean(photo);

        Button skip = new Button("Skip", FORWARD.create(), e -> save(Action.SKIP));
        Button save = new Button("Save Work", ENTER.create(), e -> save(Action.SAVE));
        Button complete = new Button("Complete", CHECK.create(), e -> save(Action.COMPLETE));

        Button newFigureButton = new Button("New Figure", PLUS.create(), e -> figureEditor.createFigure());

        details = new VerticalLayout();
        details.setWidthFull();

        HorizontalLayout actions = new HorizontalLayout(skip, newFigureButton, save, complete);
        VerticalLayout verticalLayout = new VerticalLayout(details, actions);
        add(verticalLayout, figureSelector);

        binder.bind(figureSelector, Photo::getFigures, Photo::setFigures);

        details.removeAll();
        details.add(new Image(getImageUrl(this.photo, 500), "N/A"));
        String tagsString = Arrays.stream(this.photo.getTags())
                .map(t -> "#" + t)
                .collect(Collectors.joining(", "));
        TextArea tagsArea = new TextArea();
        tagsArea.setValue(tagsString);
        tagsArea.setWidth("100%");
        tagsArea.setEnabled(false);
        details.add(tagsArea);
    }

    private enum Action {
        SKIP, SAVE, COMPLETE
    }

    private void save(Action action) {
        switch (action) {
            case COMPLETE:
                photo.setCompleted(true);
                photoService.save(photo);
                eventBus.publish(this, new PhotoChangedEvent(photo, UPDATED));
                break;
            case SAVE:
                photoService.save(photo);
                eventBus.publish(this, new PhotoChangedEvent(photo, UPDATED));
                break;
            case SKIP:
            default:
        }
        details.removeAll();
        this.photo = null;
        binder.removeBean();
        photoService.anyNotCompleted().ifPresent(this::nextPhoto);
    }

    @EventBusListenerMethod
    public void update(FigureChangedEvent event) {
        if (photo != null && event.getOperation().equals(CREATED)) {
            Figure figure = event.getValue();
            boolean assigned = figure.getPhotos().stream().anyMatch(p -> p.getId().equals(photo.getId()));
            if (assigned) {
                ValueSetHelper.add(figureSelector, figure);
            } else {
                ValueSetHelper.remove(figureSelector, figure);
            }
        }
    }

}
