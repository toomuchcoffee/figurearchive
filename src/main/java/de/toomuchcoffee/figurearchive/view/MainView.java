package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import de.toomuchcoffee.figurearchive.entitiy.Figure;
import de.toomuchcoffee.figurearchive.entitiy.ProductLine;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import de.toomuchcoffee.figurearchive.service.ImportService;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Collectors;

@Route
public class MainView extends VerticalLayout {

    private final FigureRepository figureRepository;
    private final ImportService importService;

    private final Grid<Figure> grid = new Grid<>(Figure.class);

    private FilterParams filterParams = new FilterParams();

    @Autowired
    private HttpServletRequest request;

    @Getter
    @Setter
    private static class FilterParams {
        private String verbatim;
        private ProductLine productLine;
    }

    public MainView(FigureRepository figureRepository, ImportService importService, FigureEditor figureEditor) {
        this.figureRepository = figureRepository;
        this.importService = importService;

        Button addNewBtn = new Button("New figure", VaadinIcon.PLUS.create());

        MemoryBuffer buffer = new MemoryBuffer();
        Upload csvUpload = new Upload(buffer);
        csvUpload.setDropAllowed(false);
        csvUpload.setUploadButton(new Button("Select CSV file", VaadinIcon.UPLOAD.create()));
        csvUpload.setAcceptedFileTypes(".csv");

        Button btnLogout = new Button("Logout", VaadinIcon.EXIT.create(), e -> requestLogout());

        HorizontalLayout actions = new HorizontalLayout(addNewBtn, csvUpload, btnLogout);
        add(actions, grid, figureEditor);

        grid.setHeightByRows(true);
        grid.setColumns("placementNo", "verbatim", "productLine", "year");
        grid.getColumnByKey("placementNo").setWidth("150px").setFlexGrow(0);

        grid.addComponentColumn(f -> f.getImage() == null ? new Span() : new Image(f.getImage(), "n/a")).setHeader("Image");

        ComboBox<String> cbVerbatimFilter = new ComboBox<>("Verbatim");
        cbVerbatimFilter.setItems(figureRepository.findAll().stream().map(Figure::getVerbatim).sorted().collect(Collectors.toList()));
        ComboBox<ProductLine> cbProductLineFilter = new ComboBox<>("Product line");
        cbProductLineFilter.setItems(ProductLine.values());

        cbVerbatimFilter.addValueChangeListener(e -> {
            filterParams.setVerbatim(e.getValue());
            listFigures();
        });

        cbProductLineFilter.addValueChangeListener(e -> {
            filterParams.setProductLine(e.getValue());
            listFigures();
        });

        csvUpload.addSucceededListener(e -> {
            Notification.show("YAY");
            importCsv(buffer.getInputStream());
            listFigures();
        });

        HorizontalLayout filter = new HorizontalLayout();
        filter.add(cbVerbatimFilter);
        filter.add(cbProductLineFilter);

        add(filter, grid);

        grid.asSingleSelect().addValueChangeListener(e -> Optional.ofNullable(e.getValue()).ifPresent(figureEditor::editFigure));

        addNewBtn.addClickListener(e -> figureEditor.createFigure());

        figureEditor.setChangeHandler(() -> {
            figureEditor.setVisible(false);
            listFigures();
        });

        listFigures();
    }

    @SneakyThrows
    private void importCsv(InputStream is) {
        byte[] bytes = IOUtils.toByteArray(is);
        importService.importCsv(bytes);
    }

    private void listFigures() {
        if (StringUtils.isEmpty(filterParams.getVerbatim()) && filterParams.getProductLine() == null) {
            grid.setItems(figureRepository.findAll());
        } else if (StringUtils.isEmpty(filterParams.getVerbatim()) && filterParams.getProductLine() != null) {
            grid.setItems(figureRepository.findByProductLine(filterParams.getProductLine()));
        } else if (!StringUtils.isEmpty(filterParams.getVerbatim()) && filterParams.getProductLine() == null) {
            grid.setItems(figureRepository.findByVerbatimStartsWithIgnoreCase(filterParams.getVerbatim()));
        } else {
            grid.setItems(figureRepository.findByVerbatimStartsWithIgnoreCaseAndProductLine(filterParams.getVerbatim(), filterParams.getProductLine()));
        }

    }

    private void requestLogout() {
        SecurityContextHolder.clearContext();
        request.getSession(false).invalidate();

        UI.getCurrent().getSession().close();
        UI.getCurrent().getPage().reload();// to redirect user to the login page
    }

}
