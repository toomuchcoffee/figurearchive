package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.event.FigureImportEvent;
import de.toomuchcoffee.figurearchive.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

import static com.vaadin.flow.component.icon.VaadinIcon.UPLOAD;
import static com.vaadin.flow.component.notification.Notification.Position.MIDDLE;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class FigureImport extends Upload {
    private final ImportService importService;
    private final EventBus.UIEventBus eventBus;

    @PostConstruct
    public void init() {
        setReceiver(new MemoryBuffer());
        setDropAllowed(false);
        setUploadButton(new Button("CSV Import", UPLOAD.create()));
        setAcceptedFileTypes(".csv");
        addSucceededListener(e -> {
            importCsv(((MemoryBuffer) getReceiver()).getInputStream());
        });
    }

    private void importCsv(InputStream is) {
        try {
            byte[] bytes = IOUtils.toByteArray(is);
            List<Figure> figures = importService.importCsv(bytes);
            eventBus.publish(this, new FigureImportEvent(figures));
            Notification.show(String.format("Successfully imported %s figures!", figures.size()), 5000, MIDDLE);
        } catch (Exception e) {
            Notification.show(e.getMessage(), 5000, MIDDLE);
            e.printStackTrace();
        }
    }

}
