package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static com.vaadin.flow.component.icon.VaadinIcon.CLOSE;
import static com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER;
import static org.apache.commons.lang3.StringUtils.capitalize;

class FigureSelectionNotification  {
    static void show(String action, String displayName, int count) {
        String styles = ".bold { font-weight: bold; }";
        StreamRegistration resource = UI.getCurrent().getSession().getResourceRegistry()
                .registerResource(new StreamResource("styles.css", () -> {
                    byte[] bytes = styles.getBytes(StandardCharsets.UTF_8);
                    return new ByteArrayInputStream(bytes);
                }));
        UI.getCurrent().getPage().addStyleSheet("base://" + resource.getResourceUri().toString());

        Span s1 = new Span(capitalize(action));
        Span s2 = new Span(" \""+ displayName + "\". ");
        s2.addClassName("bold");
        Span s3 = new Span("Number of selected figures: ");
        Span s4 = new Span(String.valueOf(count));
        s4.addClassName("bold");

        Notification notification = new Notification();
        Button button = new Button(CLOSE.create(), e -> notification.close());
        notification.add(new HorizontalLayout(new Div(s1, s2, s3, s4), button));

        notification.setDuration(3000);
        notification.setPosition(TOP_CENTER);
        notification.open();
    }
}
