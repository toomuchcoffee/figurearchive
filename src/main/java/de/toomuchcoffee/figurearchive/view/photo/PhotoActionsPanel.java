package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoSearchEvent;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoSearchResultEvent;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import de.toomuchcoffee.figurearchive.service.PhotoService.PhotoFilter;
import de.toomuchcoffee.figurearchive.view.controls.PaginationTabs;
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
        PhotoFilter photoFilter = new PhotoFilter();
        Checkbox cbWithFigures = new Checkbox("With Figures", e -> {
            photoFilter.setWithFigures(e.getValue() == null ? false : e.getValue());
            eventBus.publish(this, new PhotoSearchEvent(photoFilter, 0));
        });

        PhotoTagFilter photoTagFilter = new PhotoTagFilter(photoRepository, e -> {
            photoFilter.setQuery(e.getValue());
            eventBus.publish(this, new PhotoSearchEvent(photoFilter, 0));
        });

        PaginationTabs pagination = new PaginationTabs<PhotoSearchResultEvent, PhotoSearchEvent, String>(
                eventBus, PhotoSearchResultEvent.class, PhotoSearchEvent.class);

        add(photoTagFilter, cbWithFigures, pagination, photoQueryInfo, tumblrSyncButton);
    }

}
