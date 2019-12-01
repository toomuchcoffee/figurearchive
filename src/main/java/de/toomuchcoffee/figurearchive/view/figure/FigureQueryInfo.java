package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.event.FigureChangedEvent;
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
    private long count;

    @PostConstruct
    public void init() {
        TextField textField = getContent();
        textField.setEnabled(false);

        addAttachListener(e -> eventBus.subscribe(this));
        addDetachListener(e -> eventBus.unsubscribe(this));
    }

    @EventBusListenerMethod
    public void update(FigureSearchResultEvent event) {
        count = event.getCount();
        getContent().setValue(count + " figures found");
    }

    @EventBusListenerMethod
    public void update(FigureChangedEvent event) {
        switch (event.getOperation()) {
            case CREATED:
                count++;
                break;
            case DELETED:
                count--;
                break;
            case UPDATED:
            default: break;
        }
        getContent().setValue(count + " figures found");
    }

}
