package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import de.toomuchcoffee.figurearchive.config.LuceneIndexConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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
import java.util.concurrent.TimeUnit;

@Push
@Route(value = "login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final Label label;
    private final TextField userNameTextField;
    private final PasswordField passwordField;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private HttpServletRequest request;

    private final LuceneIndexConfig.CustomProgressMonitor progressMonitor;

    LoginView(@Autowired LuceneIndexConfig.CustomProgressMonitor progressMonitor,
              @Autowired Environment environment,
              @Value("${figurearchive.admin-password:null}") String password) {
        this.progressMonitor = progressMonitor;

        label = new Label("F I G U R E A R C H I V E");

        userNameTextField = new TextField();
        userNameTextField.setPlaceholder("Username");

        passwordField = new PasswordField();
        passwordField.setPlaceholder("Password");

        if ("local".equals(environment.getActiveProfiles()[0])) {
            userNameTextField.setValue("admin");
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

        showIndexingProgress();
    }

    private void showIndexingProgress() {
        if (progressMonitor.isDone()) {
            return;
        }
        ProgressBar progressBar = new ProgressBar(0, 1);
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.add(progressBar);
        dialog.open();

        new Thread(() -> {
            try {
                while (!progressMonitor.isDone()) {
                    getUI().ifPresent(ui -> ui.access(() -> progressBar.setValue(progressMonitor.getProgress())));
                    TimeUnit.SECONDS.sleep(1);
                }
                dialog.close();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
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