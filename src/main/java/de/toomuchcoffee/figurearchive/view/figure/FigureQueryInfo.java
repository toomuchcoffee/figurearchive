package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.event.FigureSearchResultEvent;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class FigureQueryInfo extends Composite<TextField> {
    private final EventBus.SessionEventBus eventBus;

    @PostConstruct
    public void init() {
        TextField textField = getContent();
        textField.setEnabled(false);

        addAttachListener(e -> eventBus.subscribe(this));
        addDetachListener(e -> eventBus.unsubscribe(this));
    }

    @EventBusListenerMethod
    public void update(FigureSearchResultEvent event) {
        getContent().setValue(event.getCount() + " figures found");
    }

}
