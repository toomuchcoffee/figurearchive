package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import de.toomuchcoffee.figurearchive.util.PhotoUrlHelper;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.vaadin.flow.component.icon.VaadinIcon.EXIT;
import static com.vaadin.flow.component.icon.VaadinIcon.FILE_REMOVE;

@UIScope
@RequiredArgsConstructor
public class PhotoGallery extends FlexLayout {
    private final PhotoService photoService;

    public void update(Figure figure) {
        removeAll();

        Set<Photo> photos = figure.getPhotos();

        int rowSize = 3;
        int colSize = (int) Math.ceil(((double) photos.size()) / ((double) rowSize));

        setWrapMode(FlexLayout.WrapMode.WRAP);
        setWidth(rowSize * 75 + "px");
        setHeight(colSize * 75 + "px");
        photos.forEach(photo -> {
            Image image = new Image(PhotoUrlHelper.getImageUrl(photo, 75), "");
            image.setWidth("75px");
            image.setHeight("75px");
            add(image);
            image.addClickListener(imageClickEvent -> {
                PhotoViewer photoViewer = new PhotoViewer(figure, photo);
                photoViewer.open();
            });
        });
    }

    private class PhotoViewer extends Dialog {
        PhotoViewer(Figure figure, Photo photo) {
            Set<Photo> photos = figure.getPhotos();

            add(new Image(PhotoUrlHelper.getImageUrl(photo, 500), ""));
            Button remove = new Button("Remove Image from Figure?", FILE_REMOVE.create(), buttonClickEvent -> {
                Set<Figure> figures = photo.getFigures();
                figures.remove(figure);
                photo.setFigures(figures);
                photo.setCompleted(false);
                photoService.save(photo);
                photos.remove(photo);
                figure.setPhotos(photos);
                update(figure);
                close();
            });
            Button close = new Button("Close", EXIT.create(), buttonClickEvent -> close());
            HorizontalLayout actions = new HorizontalLayout(remove, close);
            add(actions);
        }
    }

}
