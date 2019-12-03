package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import de.toomuchcoffee.figurearchive.config.LuceneIndexConfig;
import de.toomuchcoffee.figurearchive.service.TumblrPostService;
import de.toomuchcoffee.figurearchive.view.figure.FigureEditor;
import de.toomuchcoffee.figurearchive.view.figure.FigureImport;
import de.toomuchcoffee.figurearchive.view.figure.FigurePanel;
import de.toomuchcoffee.figurearchive.view.photo.PhotoEditor;
import de.toomuchcoffee.figurearchive.view.photo.PhotoPanel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

import static com.vaadin.flow.component.icon.VaadinIcon.*;

@Route(layout = MyLayout.class)
public class MainView extends VerticalLayout implements BeforeEnterObserver {

    private final LuceneIndexConfig.CustomProgressMonitor progressMonitor;

    private final VerticalLayout content = new VerticalLayout();

    private ProgressThread thread;

    public MainView(FigurePanel figurePanel,
                    FigureEditor figureEditor,
                    FigureImport figureImport,
                    PhotoPanel photoPanel,
                    PhotoEditor photoEditor,
                    TumblrPostService tumblrPostService,
                    LuceneIndexConfig.CustomProgressMonitor progressMonitor,
                    HttpServletRequest request) {
        this.progressMonitor = progressMonitor;
        this.request = request;

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


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (!progressMonitor.isDone()) {
            thread = new ProgressThread(beforeEnterEvent.getUI(), progressMonitor);
            thread.start();
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    @RequiredArgsConstructor
    public static class ProgressThread extends Thread {
        private final UI ui;
        private final LuceneIndexConfig.CustomProgressMonitor progressMonitor;

        @Override
        public void run() {
            try {
                Dialog dialog = new Dialog();
                dialog.setCloseOnEsc(false);
                dialog.setCloseOnOutsideClick(false);
                ProgressBar progressBar = new ProgressBar(0, 1);
                dialog.add(progressBar);
                ui.access(dialog::open);

                while (!progressMonitor.isDone()) {
                    double progress = progressMonitor.getProgress();
                    ui.access(() -> progressBar.setValue(progress));
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(progress);
                    System.out.println(ui.getPushConfiguration().getPushMode());
                }
                System.out.println("DONE");
                ui.access(dialog::close);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
