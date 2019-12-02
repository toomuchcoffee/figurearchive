package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;
import de.toomuchcoffee.figurearchive.config.LuceneIndexConfig;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

@Push
@Route(value = "wait")
@RequiredArgsConstructor
public class WaitScreen extends VerticalLayout {
    private final LuceneIndexConfig.CustomProgressMonitor progressMonitor;
    private ProgressBar progressBar;
    private Thread thread;

    @PostConstruct
    public void init() {
        progressBar = new ProgressBar(0, 1);
        Label text = new Label("Indexing is still going on. Please wait and reload page in a couple of seconds, since Vaadin Push isn't working here for some undocumented reason....");
        add(text, progressBar);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        thread = new ProgressThread(attachEvent.getUI(), progressBar, progressMonitor);
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        thread.interrupt();
        thread = null;
    }

    @RequiredArgsConstructor
    static class ProgressThread extends Thread {
        private final UI ui;
        private final ProgressBar progressBar;
        private final LuceneIndexConfig.CustomProgressMonitor progressMonitor;

        @Override
        public void run() {
            try {
                while (!progressMonitor.isDone()) {
                    ui.access(() -> progressBar.setValue(progressMonitor.getProgress()));
                    //TimeUnit.SECONDS.sleep(1);
                }
                ui.access(() -> ui.navigate("login"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}