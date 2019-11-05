package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Tag;
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
public class ImageSelector extends AbstractCompositeField<VerticalLayout, ImageSelector, Set<Photo>> {
    private final PhotoService photoService;

    private static final Set<Photo> DEFAULT_VALUE = new HashSet<>();

    private ImageGallery availableImages = new ImageGallery(75,5, 2, (photo) -> {
        this.getValue().add(photo);
        updateSelectedImage();
    });
    private ImageGallery selectedImages = new ImageGallery(250, 2, 1, (photo) -> {
        this.getValue().remove(photo);
        updateSelectedImage();
    });

    public ImageSelector(PhotoService photoService) {
        super(DEFAULT_VALUE);
        this.photoService = photoService;

        getContent().add(selectedImages);
        getContent().add(availableImages);

        addValueChangeListener(e -> updateSelectedImage());
    }

    private void updateSelectedImage() {
        selectedImages.update(new ArrayList<>(getValue()));
    }

    @Override
    protected void setPresentationValue(Set<Photo> s) {
    }

    public void updateImageGallery(String verbatim) {
        List<Photo> photos = photoService.findPhotosForVerbatim(verbatim);
        availableImages.update(photos);
    }

}
