package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.service.PhotoService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UIScope
@SpringComponent
@Tag("div")
public class ImageSelector extends AbstractCompositeField<HorizontalLayout, ImageSelector, Set<Photo>> {
    private final PhotoService photoService;

    private static final Set<Photo> DEFAULT_VALUE = new HashSet<>();

    private ImageGallery imageGallery = new ImageGallery(75,5, 5, (photos) -> this.setValue(photos));
    private ImageGallery selectedImage = new ImageGallery(500, 1, 1, (photos) -> this.setValue(DEFAULT_VALUE));

    public ImageSelector(PhotoService photoService) {
        super(DEFAULT_VALUE);
        this.photoService = photoService;

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(selectedImage);
        verticalLayout.add(imageGallery);
        getContent().add(verticalLayout);

        updateSelectedImage();
        addValueChangeListener(e -> updateSelectedImage());
    }

    private void updateSelectedImage() {
        selectedImage.update(new ArrayList<>(getValue()));
    }

    @Override
    protected void setPresentationValue(Set<Photo> s) {
    }

    public void updateImageGallery(String verbatim) {
        List<Photo> photos = photoService.findPhotosForVerbatim(verbatim);
        imageGallery.update(photos);
    }

}
