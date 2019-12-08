package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.event.PhotoSearchEvent;
import de.toomuchcoffee.figurearchive.event.PhotoSearchResultEvent;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import de.toomuchcoffee.figurearchive.view.controls.PaginationTabs;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.component.orderedlayout.FlexLayout.WrapMode.WRAP;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class PhotoActionsPanel extends FlexLayout {

    private final PhotoService photoService;
    private final EventBus.UIEventBus eventBus;

    @PostConstruct
    public void init() {
        setWrapMode(WRAP);
        ComboBox<String> tagFilter = new ComboBox<>();
        tagFilter.setPlaceholder("Filter by Tag");
        tagFilter.setClearButtonVisible(true);
        tagFilter.addValueChangeListener(e ->
                eventBus.publish(this, new PhotoSearchEvent(e.getValue(), 0)));

        PaginationTabs pagination = new PaginationTabs<PhotoSearchResultEvent, PhotoSearchEvent, String>(
                eventBus, PhotoSearchResultEvent.class, PhotoSearchEvent.class);

        add(tagFilter, pagination);

        addAttachListener(e -> {
            tagFilter.setItems(photoService.getAllTags());
            eventBus.publish(this, new PhotoSearchEvent("", 0));
        });
    }

}
