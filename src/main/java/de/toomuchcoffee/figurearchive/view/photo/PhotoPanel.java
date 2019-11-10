package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.ConfigProperties;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.service.PhotoService.PhotoFilter;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.Optional;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class PhotoPanel extends VerticalLayout {

    private final ConfigProperties properties;
    private final ConfigurableFilterDataProvider<Photo, Void, PhotoFilter> figureDataProvider;
    private final PhotoActionsPanel photoActionsPanel;
    private final PhotoEditor photoEditor;

    @PostConstruct
    public void init() {
        PhotoGrid photoGrid = new PhotoGrid(figureDataProvider, properties,
                e -> Optional.ofNullable(e.getValue()).ifPresent(photoEditor::editPhoto));

        add(photoActionsPanel, photoGrid);
    }

}
