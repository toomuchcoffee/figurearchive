package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.util.PhotoUrlHelper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@UIScope
@RequiredArgsConstructor
public class PhotoGallery extends FlexLayout {
    private final int tileSize;

    public void update(List<Photo> photos) {
        removeAll();

        setWrapMode(FlexLayout.WrapMode.WRAP);
        setHeightFull();
        photos.forEach(photo -> add(new Image(PhotoUrlHelper.getImageUrl(photo, tileSize), "")));
    }

}
