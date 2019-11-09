package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.FigureModifiedEvent;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Optional;

import static com.vaadin.flow.component.icon.VaadinIcon.ROTATE_RIGHT;
import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;
import static java.util.stream.Collectors.joining;

@SpringComponent
@UIScope
@RequiredArgsConstructor
public class PhotoEditor extends VerticalLayout {

    private final ConfigurableFilterDataProvider<Figure, Void, FigureFilter> figureDataProvider;
    private final PhotoRepository repository;
    private final EventBus.ApplicationEventBus eventBus;

    private Image image;
    private Photo photo;

    private UnorderedList figureList;

    @PostConstruct
    public void init() {
        repository.findTop1ByFiguresIsEmpty().ifPresent(this::updateComponent);

        eventBus.subscribe(this);
    }

    private void updateComponent(Photo photo) {
        removeAll();

        this.photo = photo;

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        VerticalLayout verticalLayout = new VerticalLayout();

        this.image = new Image();
        image.setSrc(getImageUrl(photo, 250));
        verticalLayout.add(image);

        TextArea tags = new TextArea("Tags");
        tags.setHeight("50%");
        tags.setReadOnly(true);
        tags.setValue(Arrays.stream(photo.getTags()).map(t -> "#" + t).collect(joining(", ")));

        figureList = new UnorderedList();
        updateFigureList();

        FigureGrid figureGrid = new FigureGrid(figureDataProvider, e -> {
            Optional.ofNullable(e.getValue()).ifPresent(figure -> {
                photo.getFigures().add(figure);
                figure.getPhotos().add(photo);
                updateFigureList();
            });
        });
        horizontalLayout.add(verticalLayout, tags, figureList);

        Button save = new Button("Save & next", ROTATE_RIGHT.create(), e -> saveAndNext());
        add(figureGrid, horizontalLayout, save);
    }

    @EventBusListenerMethod
    public void update(FigureModifiedEvent event) {
        Optional<Photo> photo = repository.findById(this.photo.getId());
        if (photo.isPresent()) {
            updateComponent(photo.get());
        } else {
            init();
        }
    }

    private void updateFigureList() {
        figureList.removeAll();
        photo.getFigures().forEach(figure -> figureList.add(new ListItem(getDisplayName(figure))));
    }

    private String getDisplayName(Figure figure) {
        return figure.getVerbatim() + Optional.ofNullable(figure.getProductLine()).map(l -> ", " + l.name()).orElse("");
    }

    private void saveAndNext() {
        repository.save(photo);
        figureDataProvider.refreshAll();

        repository.findTop1ByFiguresIsEmpty().ifPresent(this::updateComponent);
    }

}
