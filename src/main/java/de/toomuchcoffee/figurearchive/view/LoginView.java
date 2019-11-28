package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Route(value = "login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final Label label;
    private final TextField userNameTextField;
    private final PasswordField passwordField;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private HttpServletRequest request;

    LoginView(@Value("${figurearchive.admin-password:null}") String password) {
        label = new Label("F I G U R E A R C H I V E");

        userNameTextField = new TextField();
        userNameTextField.setPlaceholder("Username");
        userNameTextField.setValue("admin");

        passwordField = new PasswordField();
        passwordField.setPlaceholder("Password");
        if (password != null) {
            passwordField.setValue(password);
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

            HttpSession session = request.getSession(false);
            DefaultSavedRequest savedRequest = (DefaultSavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
            String requestedURI = savedRequest != null ? savedRequest.getRequestURI() : "/";

            this.getUI().ifPresent(ui -> ui.navigate(StringUtils.removeStart(requestedURI, "/")));
        } catch (BadCredentialsException e) {
            label.setText("Invalid username or password. Please try again.");
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            beforeEnterEvent.rerouteTo("");
        }
    }
}