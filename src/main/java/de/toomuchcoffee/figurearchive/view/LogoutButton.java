package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import static com.vaadin.flow.component.icon.VaadinIcon.EXIT;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class LogoutButton extends Button {

    private final HttpServletRequest request;

    @PostConstruct
    public void init() {
        setText("Logout");
        setIcon(EXIT.create());
        addClickListener(e -> requestLogout());
    }

    private void requestLogout() {
        SecurityContextHolder.clearContext();
        request.getSession(false).invalidate();

        UI.getCurrent().getSession().close();
        UI.getCurrent().getPage().reload();
    }
}
