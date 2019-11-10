package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.component.icon.VaadinIcon.CHECK;
import static com.vaadin.flow.component.icon.VaadinIcon.EXIT;
import static de.toomuchcoffee.figurearchive.config.EventBusConfig.DataChangedEvent.DUMMY;
import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;

@SpringComponent
@UIScope
@RequiredArgsConstructor
public class PhotoEditor extends Dialog implements KeyNotifier {

    private final PhotoRepository repository;
    private final FigureSelector figureSelector;
    //private final EventBus.SessionEventBus sessionEventBus;
    private final EventBus.ApplicationEventBus applicationEventBus;

    private Photo photo;

    private Button save = new Button("Save", CHECK.create(), e -> save());
    private Button cancel = new Button("Cancel", EXIT.create(), e -> close());
    private HorizontalLayout actions = new HorizontalLayout(save, cancel);

    private Binder<Photo> binder;

    private Div imageDiv = new Div();

    @PostConstruct
    public void init() {
        VerticalLayout verticalLayout = new VerticalLayout(imageDiv, figureSelector, actions);
        add(verticalLayout);

        binder = new Binder<>();
        binder.bind(figureSelector, Photo::getFigures, Photo::setFigures);

        addKeyPressListener(Key.ENTER, e -> save());
    }

    private void save() {
        repository.save(photo);
        imageDiv.removeAll();
        applicationEventBus.publish(this, DUMMY);
        close();
    }

    final void editPhoto(Photo figure) {
        open();
        this.photo = repository.findById(figure.getId())
                .orElseThrow(() -> new IllegalStateException("Couldn't find item from list"));
        binder.setBean(this.photo);
        imageDiv.removeAll();
        imageDiv.add(new Image(getImageUrl(photo, 500), "N/A"));
    }

}
