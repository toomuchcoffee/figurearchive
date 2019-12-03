package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.shared.ui.Transport;

@Push(transport = Transport.WEBSOCKET_XHR)
public class MyLayout extends VerticalLayout implements RouterLayout {

    MyLayout() {
        setHeightFull();
    }
}