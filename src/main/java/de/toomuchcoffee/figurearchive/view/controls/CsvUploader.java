package de.toomuchcoffee.figurearchive.view.controls;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService;
import de.toomuchcoffee.figurearchive.service.ImportService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import javax.annotation.PostConstruct;
import java.io.InputStream;

import static com.vaadin.flow.component.icon.VaadinIcon.UPLOAD;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class CsvUploader extends Upload {
    private final ImportService importService;
    private final ConfigurableFilterDataProvider<Figure, Void, FigureService.FigureFilter> figureDataProvider;

    @PostConstruct
    public void init() {
        setReceiver(new MemoryBuffer());
        setDropAllowed(false);
        setUploadButton(new Button("CSV Import", UPLOAD.create()));
        setAcceptedFileTypes(".csv");
        addSucceededListener(e -> {
            Notification.show("YAY");
            importCsv(((MemoryBuffer) getReceiver()).getInputStream());
        });
    }

    @SneakyThrows
    private void importCsv(InputStream is) {
        byte[] bytes = IOUtils.toByteArray(is);
        importService.importCsv(bytes);
        figureDataProvider.refreshAll();
    }

}
