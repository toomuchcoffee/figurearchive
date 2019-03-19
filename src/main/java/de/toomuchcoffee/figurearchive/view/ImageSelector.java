package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.service.ImageService;

import java.util.List;

@SpringComponent
@Tag("div")
@UIScope
public class ImageSelector extends AbstractCompositeField<HorizontalLayout, ImageSelector, String> {

    private final ImageService imageService;

    private VerticalLayout imageGallery;

    public ImageSelector(ImageService imageService) {
        super("");

        this.imageService = imageService;

        imageGallery = new VerticalLayout();

        getContent().add(imageGallery);
    }

    @Override
    protected void setPresentationValue(String s) {
    }

    public void updateImageUrls(String verbatim) {
        imageGallery.removeAll();

        List<String> imageUrls = imageService.getImages(verbatim, 49);

        HorizontalLayout row = new HorizontalLayout();
        for (int i = 0; i < imageUrls.size(); i++) {
            if (i % 7 == 0) {
                row = new HorizontalLayout();
            }
            imageGallery.add(row);

            String imageUrl = imageUrls.get(i);
            Div div = new Div();
            Image image = new Image();
            image.setSrc(imageUrl);
            div.add(image);
            Checkbox checkbox = new Checkbox();
            checkbox.addValueChangeListener(e -> {
                if (e.getValue()) {
                    ImageSelector.this.setValue(imageUrl);
                } else {
                    ImageSelector.this.setValue(null);
                }
            });
            div.add(checkbox);

            row.add(div);
        }
    }

}
