package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.router.Route;
import de.toomuchcoffee.figurearchive.entitiy.Figure;
import de.toomuchcoffee.figurearchive.entitiy.ProductLine;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import de.toomuchcoffee.figurearchive.service.ImportService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Optional;

import static com.vaadin.flow.component.icon.VaadinIcon.*;
import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

@Route
@RequiredArgsConstructor
public class MainView extends VerticalLayout {

    private final ConfigurableFilterDataProvider<Figure, Void, FigureFilter> figureDataProvider;
    private final FigureDataInfo figureDataInfo;
    private final ImportService importService;
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
        Grid<Figure> grid = new Grid<>(Figure.class);
        add(actions, grid);

        grid.setDataProvider(figureDataProvider);
        grid.setPageSize(1000); // TODO
        grid.setHeightByRows(true);
        grid.setColumns("placementNo", "verbatim", "productLine", "year");
        grid.getColumnByKey("placementNo").setWidth("150px").setFlexGrow(0);

        grid.addComponentColumn(f -> f.getImage() == null ? new Span() : new Image(f.getImage(), "n/a")).setHeader("Image");

        FigureFilter figureFilter = new FigureFilter();

        TextField tfVerbatimFilter = new TextField("Verbatim");
        tfVerbatimFilter.setPlaceholder("Filter by verbatim");
        tfVerbatimFilter.setValueChangeMode(EAGER);
        tfVerbatimFilter.addValueChangeListener(e -> {
            figureFilter.setFilterText(e.getValue());
            figureDataProvider.setFilter(figureFilter);
        });

        ComboBox<ProductLine> cbProductLineFilter = new ComboBox<>("Product line");
        cbProductLineFilter.setItems(ProductLine.values());
        cbProductLineFilter.addValueChangeListener(e -> {
            figureFilter.setProductLine(e.getValue());
            figureDataProvider.setFilter(figureFilter);
        });

        HorizontalLayout filter = new HorizontalLayout();
        filter.add(tfVerbatimFilter);
        filter.add(cbProductLineFilter);
        filter.add(figureDataInfo);

        add(filter, grid);

        grid.asSingleSelect().addValueChangeListener(e -> Optional.ofNullable(e.getValue()).ifPresent(figureEditor::editFigure));
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
