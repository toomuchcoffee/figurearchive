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
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.service.PhotoService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;

@UIScope
@SpringComponent
@Tag("div")
public class ImageSelector extends AbstractCompositeField<HorizontalLayout, ImageSelector, Set<Photo>> {
    private final PhotoService photoService;

    private static final int MAX_PAGE_SIZE = 49;

    private VerticalLayout imageGallery = new VerticalLayout();

    private Button btnRemoveImage = new Button(VaadinIcon.TRASH.create());

    public ImageSelector(PhotoService photoService) {
        super(new HashSet<>());

        this.photoService = photoService;

        updateSelectedImage();

        addValueChangeListener(e -> updateSelectedImage());

        btnRemoveImage.addClickListener(e -> setValue(new HashSet<>()));
    }

    private void updateSelectedImage() {
        getContent().removeAll();
        if (getValue().isEmpty()) {
            getContent().add(imageGallery);
        } else {
            AbsoluteLayout div = new AbsoluteLayout();
            div.setWidth("500px");
            div.setHeight("750px");
            Image image = new Image(getImageUrl(getValue().iterator().next(), 500), "N/A");
            image.setWidth("500px");
            div.add(image, 0, 0);
            div.add(btnRemoveImage,0, 450);
            getContent().add(div);
        }
    }

    @Override
    protected void setPresentationValue(Set<Photo> s) {
    }

    public void updateImageGallery(String verbatim) {
        imageGallery.removeAll();

        List<Photo> imageUrls = photoService.getThumbnails(verbatim);

        int startIndex = 0;
        int offset = startIndex * MAX_PAGE_SIZE;

        // List<Photo> page = imageUrls.subList(offset, offset + MAX_PAGE_SIZE);

        int pageSize = Math.min(imageUrls.size(), MAX_PAGE_SIZE);

        HorizontalLayout row = new HorizontalLayout();
        for (int i = 0; i < pageSize; i++) {
            if (i % 7 == 0) {
                row = new HorizontalLayout();
            }
            imageGallery.add(row);
            Photo photo = imageUrls.get(i);
            AbsoluteLayout div = new AbsoluteLayout();
            div.setHeight("75px");
            div.setWidth("75px");
            Image image = new Image();
            image.setSrc(getImageUrl(photo, 75));
            div.add(image, 0, 0);
            Button button = new Button(VaadinIcon.PLUS.create());
            button.addClickListener(e -> ImageSelector.this.setValue(newHashSet(photo)));
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
