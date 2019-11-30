package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.event.FigureChangedEvent;
import de.toomuchcoffee.figurearchive.service.FigureService;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import de.toomuchcoffee.figurearchive.view.photo.FigureList;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static com.vaadin.flow.component.icon.VaadinIcon.*;
import static de.toomuchcoffee.figurearchive.event.EntityChangedEvent.Operation.*;
import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static org.jsoup.helper.StringUtil.isBlank;

@SpringComponent
@UIScope
@RequiredArgsConstructor
public class FigureEditor extends Dialog implements KeyNotifier {

    private final FigureService figureService;
    private final PhotoService photoService;
    private final EventBus.SessionEventBus eventBus;

    private PhotoGallery photoGallery;
    private final FigureList similarFigures = new FigureList();

    private Figure figure;

    private TextField tfVerbatim = new TextField(null, "Verbatim");
    private TextField tfPlacementNo = new TextField(null, "Placement No.");
    private ComboBox<Short> cbYear = new ComboBox<>();
    private ComboBox<ProductLine> cbLine = new ComboBox<>();

    private Button save = new Button("Save", CHECK.create(), e -> save());
    private Button cancel = new Button("Cancel", EXIT.create(), e -> resetAndClose());
    private Button delete = new Button("Delete", TRASH.create(), e -> delete());
    private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private Binder<Figure> binder;

    @PostConstruct
    public void init() {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setHeight("400px");
        add(wrapper);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setHeightFull();
        wrapper.add(horizontalLayout);
        FormLayout attributes = new FormLayout();
        attributes.setMinWidth("100px");
        attributes.setMaxWidth("200px");
        attributes.add(tfVerbatim, cbLine, cbYear, tfPlacementNo);
        photoGallery = new PhotoGallery(photoService);
        horizontalLayout.add(attributes, photoGallery, similarFigures);
        photoGallery.setVisible(false);
        similarFigures.setVisible(false);
        similarFigures.setHeader(new Label("Similar Existing Figures"));
        wrapper.add(actions);

        tfVerbatim.setValueChangeMode(ValueChangeMode.EAGER);
        tfVerbatim.addValueChangeListener(e -> similarFigures.update(isBlank(e.getValue()) ? newArrayList() : similarFigures(e.getValue())));

        cbYear.setPlaceholder("Year");
        cbYear.setItems(IntStream.range(1977, now().getYear() + 1).mapToObj(value -> (short) value).collect(toList()));
        cbYear.setClearButtonVisible(true);
        cbLine.setPlaceholder("Product Line");
        cbLine.setItems(ProductLine.values());
        cbLine.setItemLabelGenerator(ProductLine::name);
        cbLine.setClearButtonVisible(true);

        binder = new Binder<>();
        binder.bind(tfVerbatim, Figure::getVerbatim, Figure::setVerbatim);
        binder.bind(tfPlacementNo, Figure::getPlacementNo, Figure::setPlacementNo);
        binder.bind(cbYear, Figure::getYear, Figure::setYear);
        binder.bind(cbLine, Figure::getProductLine, Figure::setProductLine);
    }

    private List<Figure> similarFigures(String query) {
        return figureService.fuzzySearch(query).stream()
                .limit(50)
                .collect(toList());
    }

    private void delete() {
        figureService.delete(figure);
        eventBus.publish(this, new FigureChangedEvent(figure, DELETED));

        resetAndClose();
    }

    private void save() {
        boolean isNew = figure.getId() == null;
        figureService.save(figure);
        eventBus.publish(this, new FigureChangedEvent(figure, isNew ? CREATED : UPDATED));

        resetAndClose();
    }

    private void resetAndClose() {
        this.figure = null;
        this.binder.removeBean();
        photoGallery.setVisible(false);
        similarFigures.setVisible(false);
        close();
    }

    public final void createFigure() {
        open();
        this.figure = new Figure();
        binder.setBean(this.figure);
        photoGallery.update(figure);
        photoGallery.setVisible(false);
        similarFigures.setVisible(true);
        tfVerbatim.focus();
    }

    final void editFigure(Figure figure) {
        open();
        this.figure = figure;
        binder.setBean(this.figure);
        photoGallery.update(figure);
        photoGallery.setVisible(true);
        similarFigures.setVisible(false);
        tfVerbatim.focus();
    }

}
