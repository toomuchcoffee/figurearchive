package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoSearchEvent;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import de.toomuchcoffee.figurearchive.view.controls.TumblrSyncButton;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class PhotoActionsPanel extends HorizontalLayout {

    private final EventBus.SessionEventBus eventBus;
    private final PhotoQueryInfo photoQueryInfo;
    private final TumblrSyncButton tumblrSyncButton;
    private final PhotoRepository photoRepository;

    @PostConstruct
    public void init() {
        PhotoTagFilter photoTagFilter = new PhotoTagFilter(
                photoRepository, e -> eventBus.publish(this, new PhotoSearchEvent(e.getValue())));

        add(photoTagFilter, photoQueryInfo, tumblrSyncButton);
    }
}
