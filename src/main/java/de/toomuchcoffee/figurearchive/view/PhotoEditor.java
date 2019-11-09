package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import de.toomuchcoffee.figurearchive.service.PhotoService.PhotoFilter;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.component.icon.VaadinIcon.CHECK;
import static com.vaadin.flow.component.icon.VaadinIcon.EXIT;

@SpringComponent
@UIScope
@RequiredArgsConstructor
public class PhotoEditor extends Dialog implements KeyNotifier {

    private final ConfigurableFilterDataProvider<Photo, Void, PhotoFilter> photoDataProvider;
    private final PhotoRepository repository;
    private final FigureSelector figureSelector;
    private final EventBus.SessionEventBus sessionEventBus;
    // private final EventBus.ApplicationEventBus applicationEventBus;

    private Photo photo;

    private Button save = new Button("Save", CHECK.create(), e -> save());
    private Button cancel = new Button("Cancel", EXIT.create(), e -> close());
    private HorizontalLayout actions = new HorizontalLayout(save, cancel);

    private Binder<Photo> binder;

    @PostConstruct
    public void init() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        add(horizontalLayout);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(actions);
        horizontalLayout.add(verticalLayout, figureSelector);

        binder = new Binder<>();
        binder.bind(figureSelector, Photo::getFigures, Photo::setFigures);

        horizontalLayout.setSpacing(true);

        addKeyPressListener(Key.ENTER, e -> save());
    }

    private void save() {
        repository.save(photo);
        photoDataProvider.refreshAll();
        //applicationEventBus.publish(this, SAVED);
        close();
    }

    final void editPhoto(Photo figure) {
        open();
        this.photo = repository.findById(figure.getId())
                .orElseThrow(() -> new IllegalStateException("Couldn't find item from list"));
        binder.setBean(this.photo);
    }

}
