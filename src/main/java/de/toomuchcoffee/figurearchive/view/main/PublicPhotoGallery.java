package de.toomuchcoffee.figurearchive.view.main;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.util.PhotoUrlHelper;

import java.util.Set;

import static com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap.WRAP;

class PublicPhotoGallery extends FlexLayout {
    PublicPhotoGallery(Set<Photo> photos) {
        int rowSize = 3;
        int colSize = (int) Math.ceil(((double) photos.size()) / ((double) rowSize));

        setFlexWrap(WRAP);
        setWidth(rowSize * 75 + "px");
        setHeight(colSize * 75 + "px");
        photos.forEach(photo -> {
            Image image = new Image(PhotoUrlHelper.getImageUrl(photo, 75), "");
            image.setWidth("75px");
            image.setHeight("75px");
            add(image);
            image.addClickListener(imageClickEvent -> {
                Dialog photoViewer = new Dialog(new Image(PhotoUrlHelper.getImageUrl(photo, 500), ""));
                photoViewer.open();
            });
        });
    }

}
