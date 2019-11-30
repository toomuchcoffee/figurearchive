package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.event.PhotoSearchEvent;
import de.toomuchcoffee.figurearchive.event.PhotoSearchResultEvent;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import de.toomuchcoffee.figurearchive.view.controls.PaginationTabs;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class PhotoActionsPanel extends HorizontalLayout {

    private final PhotoService photoService;
    private final EventBus.SessionEventBus eventBus;
    private final PhotoQueryInfo photoQueryInfo;

    @PostConstruct
    public void init() {
        ComboBox<String> tagFilter = new ComboBox<>();
        tagFilter.setPlaceholder("Filter by Tag");
        tagFilter.addValueChangeListener(e ->
                eventBus.publish(this, new PhotoSearchEvent(e.getValue(), 0)));

        PaginationTabs pagination = new PaginationTabs<PhotoSearchResultEvent, PhotoSearchEvent, String>(
                eventBus, PhotoSearchResultEvent.class, PhotoSearchEvent.class);

        add(tagFilter, pagination, photoQueryInfo);

        addAttachListener(e -> {
            tagFilter.setItems(photoService.getAllTags());
            eventBus.publish(this, new PhotoSearchEvent("", 0));
        });
    }

}
