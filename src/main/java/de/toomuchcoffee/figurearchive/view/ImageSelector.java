package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.service.PhotoService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@UIScope
@SpringComponent
@Tag("div")
public class ImageSelector extends AbstractCompositeField<HorizontalLayout, ImageSelector, Set<Photo>> {
    private final PhotoService photoService;

    private static final Set<Photo> DEFAULT_VALUE = new HashSet<>();
    private static final int MAX_PAGE_SIZE = 49;

    private VerticalLayout imageGallery = new VerticalLayout();

    public ImageSelector(PhotoService photoService) {
        super(DEFAULT_VALUE);
        this.photoService = photoService;

        updateSelectedImage();
        addValueChangeListener(e -> updateSelectedImage());
    }

    private void updateSelectedImage() {
        getContent().removeAll();
        if (getValue().isEmpty()) {
            getContent().add(imageGallery);
        } else {
            Photo photo = getValue().iterator().next();
            getContent().add(new ImageButton(photo, 500, e -> setValue(DEFAULT_VALUE)));
        }
    }

    @Override
    protected void setPresentationValue(Set<Photo> s) {
    }

    public void updateImageGallery(String verbatim) {
        imageGallery.removeAll();

        List<Photo> imageUrls = photoService.findPhotosForVerbatim(verbatim);
        int pageSize = Math.min(imageUrls.size(), MAX_PAGE_SIZE);

        HorizontalLayout row = new HorizontalLayout();
        for (int i = 0; i < pageSize; i++) {
            if (i % 7 == 0) {
                row = new HorizontalLayout();
            }
            imageGallery.add(row);

            Photo photo = imageUrls.get(i);
            row.add(new ImageButton(photo, 75, e -> setValue(newHashSet(photo))));
        }
    }


}
