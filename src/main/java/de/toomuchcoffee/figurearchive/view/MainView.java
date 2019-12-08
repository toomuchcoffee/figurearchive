package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import de.toomuchcoffee.figurearchive.config.LuceneIndexConfig;
import de.toomuchcoffee.figurearchive.view.main.PublicFigurePanel;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Route(value = "", layout = MyLayout.class)
public class MainView extends VerticalLayout implements BeforeEnterObserver {

    private final PublicFigurePanel publicFigurePanel;

    private final LuceneIndexConfig.CustomProgressMonitor progressMonitor;
    private final RouterLink adminLogin = new RouterLink("Admin Login", LoginView.class);

    private ProgressThread thread;

    @PostConstruct
    public void init() {
        add(adminLogin);
        add(publicFigurePanel);
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
