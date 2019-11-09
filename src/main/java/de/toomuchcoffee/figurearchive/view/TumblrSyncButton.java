package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.service.TumblrPostService;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.component.icon.VaadinIcon.ROTATE_RIGHT;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class TumblrSyncButton extends Button {

    private final TumblrPostService tumblrPostService;

    @PostConstruct
    public void init() {
        setText("Tumblr Sync");
        setIcon(ROTATE_RIGHT.create());
        addClickListener(e -> tumblrPostService.loadPosts());
    }
}
