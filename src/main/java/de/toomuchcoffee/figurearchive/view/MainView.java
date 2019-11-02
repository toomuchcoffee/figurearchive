package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import static com.vaadin.flow.component.icon.VaadinIcon.EXIT;
import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;

@Route
@RequiredArgsConstructor
public class MainView extends VerticalLayout {

    private final FigureFilterPanel figureFilterPanel;
    private final FigureGrid figureGrid;
    private final FigureEditor figureEditor;
    private final CsvUploader csvUploader;

    @Autowired
    private HttpServletRequest request;

    @PostConstruct
    public void init() {
        Button btnCreate = new Button("New figure", PLUS.create());
        btnCreate.addClickListener(e -> figureEditor.createFigure());

        Button btnLogout = new Button("Logout", EXIT.create(), e -> requestLogout());

        HorizontalLayout actions = new HorizontalLayout(btnCreate, csvUploader, btnLogout);
        add(actions);

        add(figureFilterPanel, figureGrid);
    }

    private void requestLogout() {
        SecurityContextHolder.clearContext();
        request.getSession(false).invalidate();

        UI.getCurrent().getSession().close();
        UI.getCurrent().getPage().reload();// to redirect user to the login page
    }

}
