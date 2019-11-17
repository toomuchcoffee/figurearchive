package de.toomuchcoffee.figurearchive.view.controls;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ScrollableLayout extends VerticalLayout {
    public ScrollableLayout() {
        setHeight("250px");
        setWidth(null);
        getStyle().set("overflow-y", "auto");
    }
}
