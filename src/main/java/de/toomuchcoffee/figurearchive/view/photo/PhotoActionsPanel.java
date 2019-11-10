package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoSearchEvent;
import de.toomuchcoffee.figurearchive.view.controls.TumblrSyncButton;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class PhotoActionsPanel extends HorizontalLayout {

    private final EventBus.SessionEventBus eventBus;
    private final TumblrSyncButton tumblrSyncButton;

    @PostConstruct
    public void init() {
        TextField tfFilter = new TextField();
        tfFilter.setPlaceholder("Filter by Tags");
        tfFilter.setValueChangeMode(EAGER);
        tfFilter.addValueChangeListener(e -> eventBus.publish(this, new PhotoSearchEvent(e.getValue())));

        add(tfFilter, tumblrSyncButton);
    }
}
