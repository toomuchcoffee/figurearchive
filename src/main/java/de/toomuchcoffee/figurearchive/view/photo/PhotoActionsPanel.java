package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.EventBusConfig;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoSearchEvent;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoSearchResultEvent;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import de.toomuchcoffee.figurearchive.view.controls.PaginationTabs;
import de.toomuchcoffee.figurearchive.view.controls.TumblrSyncButton;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class PhotoActionsPanel extends HorizontalLayout {

    private final EventBus.SessionEventBus eventBus;
    private final PhotoQueryInfo photoQueryInfo;
    private final TumblrSyncButton tumblrSyncButton;
    private final PhotoService photoService;

    private PhotoTagFilter photoTagFilter;

    @PostConstruct
    public void init() {
        photoTagFilter = new PhotoTagFilter(photoService, e -> {
            eventBus.publish(this, new PhotoSearchEvent(e.getValue(), 0));
        });

        PaginationTabs pagination = new PaginationTabs<PhotoSearchResultEvent, PhotoSearchEvent, String>(
                eventBus, PhotoSearchResultEvent.class, PhotoSearchEvent.class);

        add(photoTagFilter, pagination, photoQueryInfo, tumblrSyncButton);

        eventBus.subscribe(this);
    }

    @EventBusListenerMethod
    public void update(EventBusConfig.DataChangedEvent event) {
        photoTagFilter.setValue(null);
    }

}
