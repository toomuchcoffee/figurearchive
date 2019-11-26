package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.event.PhotoSearchEvent;
import de.toomuchcoffee.figurearchive.event.PhotoSearchResultEvent;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import de.toomuchcoffee.figurearchive.view.controls.PaginationTabs;
import de.toomuchcoffee.figurearchive.view.controls.TumblrSyncButton;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.data.value.ValueChangeMode.LAZY;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class PhotoActionsPanel extends HorizontalLayout {

    private final EventBus.SessionEventBus eventBus;
    private final PhotoQueryInfo photoQueryInfo;
    private final TumblrSyncButton tumblrSyncButton;
    private final PhotoService photoService;

    @PostConstruct
    public void init() {
        TextField tfPhotoTagFilter = new TextField();
        tfPhotoTagFilter.setPlaceholder("Filter by Tag");
        tfPhotoTagFilter.setValueChangeMode(LAZY);
        tfPhotoTagFilter.setValueChangeTimeout(500);
        tfPhotoTagFilter.addValueChangeListener(e ->
                eventBus.publish(this, new PhotoSearchEvent(e.getValue(), 0)));

        PaginationTabs pagination = new PaginationTabs<PhotoSearchResultEvent, PhotoSearchEvent, String>(
                eventBus, PhotoSearchResultEvent.class, PhotoSearchEvent.class);

        add(tfPhotoTagFilter, pagination, photoQueryInfo, tumblrSyncButton);

        addAttachListener(e -> eventBus.publish(this, new PhotoSearchEvent("", 0)));
    }

}
