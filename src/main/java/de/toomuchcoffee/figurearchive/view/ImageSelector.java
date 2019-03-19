package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.service.ImageService;
import org.springframework.util.StringUtils;

import java.util.List;

@SpringComponent
@Tag("div")
@UIScope
public class ImageSelector extends AbstractCompositeField<HorizontalLayout, ImageSelector, String> {

    private final ImageService imageService;

    private VerticalLayout imageGallery = new VerticalLayout();

    private Button btnRemoveImage = new Button(VaadinIcon.TRASH.create());

    public ImageSelector(ImageService imageService) {
        super("");

        this.imageService = imageService;

        updateSelectedImage();

        addValueChangeListener(e -> updateSelectedImage());

        btnRemoveImage.addClickListener(e -> setValue(""));
    }

    private void updateSelectedImage() {
        getContent().removeAll();
        if (StringUtils.isEmpty(getValue())) {
            getContent().add(imageGallery);
        } else {
            AbsoluteLayout div = new AbsoluteLayout();
            div.setWidth("500px");
            div.setHeight("750px");
            Image image = new Image(getValue().replaceFirst("_75sq.jpg", "_500.jpg"), "could not resolve image");
            image.setWidth("500px");
            div.add(image, 0, 0);
            div.add(btnRemoveImage,0, 450);
            getContent().add(div);
        }
    }

    @Override
    protected void setPresentationValue(String s) {
    }

    public void updateImageGallery(String verbatim) {
        imageGallery.removeAll();

        List<String> imageUrls = imageService.getImages(verbatim, 49);

        HorizontalLayout row = new HorizontalLayout();
        for (int i = 0; i < imageUrls.size(); i++) {
            if (i % 7 == 0) {
                row = new HorizontalLayout();
            }
            imageGallery.add(row);
            String imageUrl = imageUrls.get(i);
            AbsoluteLayout div = new AbsoluteLayout();
            div.setHeight("75px");
            div.setWidth("75px");
            Image image = new Image();
            image.setSrc(imageUrl);
            div.add(image, 0, 0);
            Button button = new Button(VaadinIcon.PLUS.create());
            button.addClickListener(e -> ImageSelector.this.setValue(imageUrl));
            div.add(button, -5, 40);
            row.add(div);
        }
    }

    public static class AbsoluteLayout extends Div {
        AbsoluteLayout() {
            getElement().getStyle().set("position", "relative");
        }

        void add(Component component, int top, int left) {
            add(component);
            component.getElement().getStyle().set("position", "absolute");
            component.getElement().getStyle().set("top", top + "px");
            component.getElement().getStyle().set("left", left + "px");
        }
    }

}
