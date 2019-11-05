package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Photo;

import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;

@UIScope
@Tag("div")
public class ImageButton extends Div {

    public ImageButton(Photo photo, int size, ComponentEventListener<ClickEvent<Button>> listener) {
        getElement().getStyle().set("position", "relative");
        getElement().getStyle().set("border", "1px solid #9E9E9E");
        getElement().getStyle().set("padding", "3px");
        getElement().getStyle().set("margin", "3px");
        setHeight(size + "px");
        setWidth(size + "px");
        Image image = new Image(getImageUrl(photo, size), "N/A");
        add(image);
        Button button = new Button();
        button.addClickListener(listener);
        button.setHeight("100%");
        button.setWidth("100%");
        add(button);
    }

    private void add(Component component) {
        super.add(component);
        component.getElement().getStyle().set("position", "absolute");
        component.getElement().getStyle().set("top", 3 + "px");
        component.getElement().getStyle().set("left", 3 + "px");
    }

}
