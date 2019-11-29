package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.toomuchcoffee.figurearchive.service.TumblrPostService;
import de.toomuchcoffee.figurearchive.view.figure.FigureEditor;
import de.toomuchcoffee.figurearchive.view.figure.FigureImport;
import de.toomuchcoffee.figurearchive.view.figure.FigurePanel;
import de.toomuchcoffee.figurearchive.view.photo.PhotoEditor;
import de.toomuchcoffee.figurearchive.view.photo.PhotoPanel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import static com.vaadin.flow.component.icon.VaadinIcon.*;

@Route
@RequiredArgsConstructor
public class MainView extends VerticalLayout {

    private final FigurePanel figurePanel;
    private final FigureEditor figureEditor;
    private final FigureImport figureImport;
    private final PhotoPanel photoPanel;
    private final PhotoEditor photoEditor;
    private final TumblrPostService tumblrPostService;

    private final VerticalLayout content = new VerticalLayout();

    @PostConstruct
    public void init() {
        MenuBar menuBar = new MenuBar();

        MenuItem figures = menuBar.addItem("Figures");
        figures.add(HANDS_UP.create());
        figures.getSubMenu()
                .addItem(menuItem(LINES_LIST, "List & Edit"), e -> select(figurePanel));
        figures.getSubMenu()
                .addItem(menuItem(PLUS_CIRCLE, "New Figure"), e -> figureEditor.createFigure());
        figures.getSubMenu()
                .addItem(menuItem(UPLOAD, "Import"), e -> select(figureImport));

        MenuItem photos = menuBar.addItem("Photos");
        photos.add(PICTURE.create());
        photos.getSubMenu()
                .addItem(menuItem(LINES_LIST, "List & Edit"), e -> select(photoPanel));
        photos.getSubMenu()
                .addItem(menuItem(CONNECT, "Assign Figures"), e -> select(photoEditor));
        photos.getSubMenu()
                .addItem(menuItem(ROTATE_RIGHT, "Tumblr Sync"), e -> tumblrPostService.loadPosts());
        MenuItem special = menuBar.addItem("Special");
        special.getSubMenu()
                .addItem(menuItem(SIGN_OUT, "Logout"), e -> requestLogout());
        add(menuBar, content);
    }

    private Component menuItem(VaadinIcon icon, String text) {
        Icon icon1 = icon.create();
        icon1.setColor("lightgrey");
        icon1.setSize("18px");
        return new HorizontalLayout(icon1, new Label(text));
    }

    private void select(Component component) {
        content.removeAll();
        content.add(component);
    }

    private final HttpServletRequest request;

    private void requestLogout() {
        SecurityContextHolder.clearContext();
        request.getSession(false).invalidate();

        UI.getCurrent().getSession().close();
        UI.getCurrent().getPage().reload();
    }

}
