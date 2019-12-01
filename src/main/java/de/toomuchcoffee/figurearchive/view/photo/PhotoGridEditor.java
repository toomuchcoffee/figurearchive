package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.event.PhotoChangedEvent;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.vaadin.flow.component.icon.VaadinIcon.*;
import static de.toomuchcoffee.figurearchive.event.EntityChangedEvent.Operation.DELETED;
import static de.toomuchcoffee.figurearchive.event.EntityChangedEvent.Operation.UPDATED;
import static de.toomuchcoffee.figurearchive.util.FigureDisplayNameHelper.getDisplayName;
import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;

@SpringComponent
@UIScope
@RequiredArgsConstructor
public class PhotoGridEditor extends Dialog implements KeyNotifier {

    private final PhotoService photoService;
    private final EventBus.SessionEventBus eventBus;

    private Photo photo;

    private HorizontalLayout details;

    @PostConstruct
    public void init() {
        Button needsWork = new Button("Needs work", QUESTION.create(), e -> save());
        Button archive = new Button("Archive", FILE_REMOVE.create(), e -> archive());
        Button cancel = new Button("Cancel", EXIT.create(), e -> resetAndClose());

        details = new HorizontalLayout();
        details.setWidth("100%");

        HorizontalLayout actions = new HorizontalLayout(needsWork, archive, cancel);
        VerticalLayout verticalLayout = new VerticalLayout(details, actions);
        add(verticalLayout);

    }

    private void updateDetails() {
        details.removeAll();
        details.add(new Image(getImageUrl(this.photo, 250), "N/A"));
        String tagsString = Arrays.stream(this.photo.getTags())
                .map(t -> "#" + t)
                .collect(Collectors.joining(", "));
        TextArea tagsArea = new TextArea();
        tagsArea.setValue(tagsString);
        tagsArea.setWidth("100%");
        tagsArea.setEnabled(false);
        details.add(tagsArea);
        UnorderedList assignedFigures = new UnorderedList();
        photo.getFigures().forEach(figure -> assignedFigures.add(new ListItem(getDisplayName(figure))));
        details.add(assignedFigures);
    }

    final void editPhoto(Photo photo) {
        open();
        this.photo = photo;
        updateDetails();
    }

    private void save() {
        photo.setCompleted(false);
        photoService.save(photo);
        details.removeAll();
        eventBus.publish(this, new PhotoChangedEvent(photo, UPDATED));

        resetAndClose();
    }

    private void archive() {
        photoService.archive(photo);
        eventBus.publish(this, new PhotoChangedEvent(photo, DELETED));
        resetAndClose();
    }

    private void resetAndClose() {
        this.photo = null;
        close();
    }

}
