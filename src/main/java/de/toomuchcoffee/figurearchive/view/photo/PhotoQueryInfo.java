package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoSearchResultEvent;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class PhotoQueryInfo extends Composite<TextField> {
    private final EventBus.SessionEventBus eventBus;

    @PostConstruct
    public void init() {
        TextField textField = getContent();
        textField.setEnabled(false);

        eventBus.subscribe(this);
    }

    @EventBusListenerMethod
    public void update(PhotoSearchResultEvent event) {
        getContent().setValue(event.getCount() + " photos found");
    }

}
