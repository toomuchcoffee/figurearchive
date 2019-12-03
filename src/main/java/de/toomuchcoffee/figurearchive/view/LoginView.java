package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Push
@RequiredArgsConstructor
@Route(value = "login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private Label label;
    private TextField userNameTextField;
    private PasswordField passwordField;

    private final AuthenticationManager authManager;

    private final HttpServletRequest request;

    private final Environment environment;

    @Value("${figurearchive.admin-password}")
    private String defaultPassword;

    @PostConstruct
    public void init() {
        label = new Label("F I G U R E A R C H I V E");

        userNameTextField = new TextField();
        userNameTextField.setPlaceholder("Username");

        passwordField = new PasswordField();
        passwordField.setPlaceholder("Password");

        if ("local".equals(environment.getActiveProfiles()[0])) {
            userNameTextField.setValue("admin");
            passwordField.setValue(defaultPassword);
        }

        passwordField.addKeyDownListener(Key.ENTER, (ComponentEventListener<KeyDownEvent>) keyDownEvent -> authenticateAndNavigate());

        Button submitButton = new Button("Login");
        submitButton.setWidth("180px");
        submitButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
            authenticateAndNavigate();
        });

        add(label, userNameTextField, passwordField, submitButton);

        setAlignItems(Alignment.CENTER);
        this.getElement().getStyle().set("height", "100%");
        this.getElement().getStyle().set("justify-content", "center");
    }

    private void authenticateAndNavigate() {
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(userNameTextField.getValue(), passwordField.getValue());
        try {
            Authentication auth = authManager.authenticate(authReq);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(auth);

            this.getUI().ifPresent(ui -> ui.navigate(""));
        } catch (BadCredentialsException e) {
            label.setText("Invalid username or password. Please try again.");
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            UI ui = beforeEnterEvent.getUI();
            ui.access(() -> ui.navigate(""));
        }
    }
}