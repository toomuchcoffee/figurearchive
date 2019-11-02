package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import de.toomuchcoffee.figurearchive.service.ImportService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

import static com.vaadin.flow.component.icon.VaadinIcon.*;

@Route
@RequiredArgsConstructor
public class MainView extends VerticalLayout {

    private final ImportService importService;
    private final FigureFilterPanel figureFilterPanel;
    private final FigureGrid figureGrid;
    private final FigureEditor figureEditor;

    @Autowired
    private HttpServletRequest request;

    @PostConstruct
    public void init() {
        Button addNewBtn = new Button("New figure", PLUS.create());
        addNewBtn.addClickListener(e -> figureEditor.createFigure());

        Upload csvUpload = new Upload(new MemoryBuffer());
        csvUpload.setDropAllowed(false);
        csvUpload.setUploadButton(new Button("Select CSV file", UPLOAD.create()));
        csvUpload.setAcceptedFileTypes(".csv");
        csvUpload.addSucceededListener(e -> {
            Notification.show("YAY");
            importCsv(((MemoryBuffer) csvUpload.getReceiver()).getInputStream());
        });

        Button btnLogout = new Button("Logout", EXIT.create(), e -> requestLogout());

        HorizontalLayout actions = new HorizontalLayout(addNewBtn, csvUpload, btnLogout);
        add(actions);

        add(figureFilterPanel, figureGrid);
    }

    @SneakyThrows
    private void importCsv(InputStream is) {
        byte[] bytes = IOUtils.toByteArray(is);
        importService.importCsv(bytes);
    }

    private void requestLogout() {
        SecurityContextHolder.clearContext();
        request.getSession(false).invalidate();

        UI.getCurrent().getSession().close();
        UI.getCurrent().getPage().reload();// to redirect user to the login page
    }

}
