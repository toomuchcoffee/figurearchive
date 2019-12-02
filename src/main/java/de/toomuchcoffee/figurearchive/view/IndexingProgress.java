package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.LuceneIndexConfig;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Push
@Route("push")
@SpringComponent
@UIScope
@RequiredArgsConstructor
public class IndexingProgress extends Dialog {
    private final LuceneIndexConfig.CustomProgressMonitor progressMonitor;
    private ProgressBar progressBar = new ProgressBar(0, 1);

    @PostConstruct
    public void init() {
        setCloseOnOutsideClick(false);
        setCloseOnEsc(false);
        add(progressBar);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        progressThread(attachEvent.getUI()).start();
    }

    private Thread progressThread(UI ui) {
        return new Thread(() -> {
            try {
                while (!progressMonitor.isDone()) {
                    ui.access(() -> progressBar.setValue(progressMonitor.getProgress()));
                    TimeUnit.SECONDS.sleep(1);
                }
                close();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
    }
}