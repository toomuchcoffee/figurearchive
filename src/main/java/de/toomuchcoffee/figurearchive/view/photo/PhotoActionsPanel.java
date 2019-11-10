package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.service.PhotoService.PhotoFilter;
import de.toomuchcoffee.figurearchive.view.controls.TumblrSyncButton;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class PhotoActionsPanel extends HorizontalLayout {

    private final ConfigurableFilterDataProvider<Photo, Void, PhotoFilter> photoDataProvider;
    private final PhotoDataInfo photoDataInfo;
    private final TumblrSyncButton tumblrSyncButton;

    @PostConstruct
    public void init() {
        TextField tfFilter = new TextField();
        tfFilter.setPlaceholder("Filter by Tags");
        tfFilter.setValueChangeMode(EAGER);
        tfFilter.addValueChangeListener(e -> photoDataProvider.setFilter(new PhotoFilter(e.getValue())));

        add(tfFilter, photoDataInfo, tumblrSyncButton);
    }
}
