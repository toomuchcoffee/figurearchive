package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import lombok.RequiredArgsConstructor;

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

    private Div imageContainer = new Div();
    private Photo photo;

    private UnorderedList figureList;
    @PostConstruct
    public void init() {
        TextArea tags = new TextArea("Tags");
        tags.setHeight("50%");
        tags.setReadOnly(true);
        figureList = new UnorderedList();
        Image image = new Image();
        imageContainer.add(image);
        repository.findTop1ByFiguresIsEmpty().ifPresent(photo -> {
            this.photo = photo;
            tags.setValue(Arrays.stream(photo.getTags()).map(t -> "#" + t).collect(joining(", ")));
            image.setSrc(getImageUrl(photo, 400));
            updateFigureList();
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        VerticalLayout verticalLayout = new VerticalLayout();
        Button save = new Button("Save & next", ROTATE_RIGHT.create(), e -> saveAndNext());
        FigureGrid figureGrid = new FigureGrid(figureDataProvider, e -> {
            Optional.ofNullable(e.getValue()).ifPresent(figure -> {
                photo.getFigures().add(figure);
                figure.getPhotos().add(photo);
                updateFigureList();
            });
        });
        verticalLayout.add(imageContainer);
        horizontalLayout.add(verticalLayout, tags, figureList);
        add(figureGrid, horizontalLayout, save);
    }


    private void updateFigureList() {
        figureList.removeAll();
        photo.getFigures().forEach(figure -> figureList.add(new ListItem(figure.getVerbatim() + ", " + figure.getProductLine().name())));
    }

    private void saveAndNext() {
        repository.save(photo);
        figureDataProvider.refreshAll();

        repository.findTop1ByFiguresIsEmpty().ifPresent(photo -> {
            this.photo = photo;
            imageContainer.removeAll();
            Image image = new Image();
            image.setSrc(getImageUrl(photo, 400));
            imageContainer.add(image);
            updateFigureList();
        });

    }


}
