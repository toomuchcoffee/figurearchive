package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import de.toomuchcoffee.figurearchive.view.controls.NewFigureButton;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.union;
import static com.vaadin.flow.component.icon.VaadinIcon.CHECK;
import static com.vaadin.flow.component.icon.VaadinIcon.EXIT;
import static de.toomuchcoffee.figurearchive.config.EventBusConfig.DataChangedEvent.DUMMY;
import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;

@SpringComponent
@UIScope
@RequiredArgsConstructor
public class PhotoEditor extends Dialog implements KeyNotifier {

    private final FigureRepository figureRepository;
    private final FigureSelector figureSelector;
    private final EventBus.SessionEventBus eventBus;
    private final NewFigureButton newFigureButton;

    private Photo photo;
    private Set<Figure> figuresBeforeChange;
    private Set<Figure> figuresAfterChange;

    private Binder<Photo> binder;

    private HorizontalLayout details;

    @PostConstruct
    public void init() {
        Button save = new Button("Save", CHECK.create(), e -> save());
        Button cancel = new Button("Cancel", EXIT.create(), e -> resetAndClose());

        newFigureButton.addClickListener(e -> this.close());

        details = new HorizontalLayout();
        details.setWidth("100%");
        HorizontalLayout actions = new HorizontalLayout(save, cancel, newFigureButton);
        VerticalLayout verticalLayout = new VerticalLayout(details, figureSelector, actions);
        add(verticalLayout);

        binder = new Binder<>();
        binder.bind(figureSelector, Photo::getFigures, Photo::setFigures);

    }

    private void updateDetails() {
        details.removeAll();
        details.add(new Image(getImageUrl(this.photo, 75), "N/A"));
        String tagsString = Arrays.stream(this.photo.getTags())
                .map(t -> "#" + t)
                .collect(Collectors.joining(", "));
        TextArea tagsArea = new TextArea();
        tagsArea.setValue(tagsString);
        tagsArea.setWidth("100%");
        tagsArea.setEnabled(false);
        details.add(tagsArea);
    }

    final void editPhoto(Photo photo) {
        open();
        this.photo = photo;
        binder.setBean(photo);
        figuresBeforeChange = photo.getFigures();
        updateDetails();
    }

    private void save() {
        figuresAfterChange = photo.getFigures();
        manageOwningSiteOfRelation();
        details.removeAll();
        eventBus.publish(this, DUMMY);

        resetAndClose();
    }

    private void resetAndClose() {
        this.photo = null;
        binder.removeBean();
        close();
    }

    private void manageOwningSiteOfRelation() {
        Set<Figure> deletedFigures = difference(figuresBeforeChange, figuresAfterChange);
        deletedFigures.forEach(f -> f.getPhotos().remove(photo));
        Set<Figure> addedFigures = difference(figuresAfterChange, figuresBeforeChange);
        addedFigures.forEach(f -> f.getPhotos().add(photo));
        figureRepository.saveAll(union(addedFigures, deletedFigures));
    }

}
