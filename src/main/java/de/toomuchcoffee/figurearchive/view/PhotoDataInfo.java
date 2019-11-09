package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoQueryEvent;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.service.PhotoService.PhotoFilter;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class PhotoDataInfo extends Composite<TextField> {
    private final ConfigurableFilterDataProvider<Photo, Void, PhotoFilter> photoDataProvider;
    private final EventBus.ApplicationEventBus eventBus;

    @PostConstruct
    public void init() {
        TextField textField = getContent();
        textField.setEnabled(false);

        eventBus.subscribe(this);
    }

    @EventBusListenerMethod
    public void update(PhotoQueryEvent event) {
        getContent().setValue(photoDataProvider.size(new Query<>()) + " photos found");
    }

}
