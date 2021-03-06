package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.ConfigProperties;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class PhotoPanel extends VerticalLayout {

    private final ConfigProperties properties;
    private final PhotoActionsPanel photoActionsPanel;
    private final PhotoGridEditor photoGridEditor;
    private final PhotoService photoService;
    private final EventBus.UIEventBus eventBus;

    @PostConstruct
    public void init() {
        PhotoGrid photoGrid = new PhotoGrid(eventBus, photoService, properties, photoGridEditor);
        add(photoActionsPanel, photoGrid);
    }

}
