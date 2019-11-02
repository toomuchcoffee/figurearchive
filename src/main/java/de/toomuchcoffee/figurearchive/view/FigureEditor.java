package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entitiy.Figure;
import de.toomuchcoffee.figurearchive.entitiy.ProductLine;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.stream.IntStream;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

@SpringComponent
@UIScope
@RequiredArgsConstructor
public class FigureEditor extends Dialog implements KeyNotifier {

    private final ConfigurableFilterDataProvider<Figure, Void, FigureFilter> figureDataProvider;
    private final FigureRepository repository;
    private final ImageSelector imageSelector;

    private Figure figure;

    private TextField tfVerbatim = new TextField("Verbatim");
    private TextField tfPlacementNo = new TextField("Placement No.");
    private ComboBox<Short> cbYear = new ComboBox<>("Year");
    private ComboBox<ProductLine> cbLine = new ComboBox<>("Line");

    private Button save = new Button("Save", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private Binder<Figure> binder;

    @PostConstruct
    public void init() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        add(horizontalLayout);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(tfVerbatim, cbLine, cbYear, tfPlacementNo, actions);
        horizontalLayout.add(verticalLayout, imageSelector);

        cbYear.setItems(IntStream.range(1977, now().getYear() + 1).mapToObj(value -> (short) value).collect(toList()));
        cbLine.setItems(ProductLine.values());
        cbLine.setItemLabelGenerator(ProductLine::name);

        binder = new Binder<>();
        binder.bind(imageSelector, Figure::getImage, Figure::setImage);
        binder.bind(tfVerbatim, Figure::getVerbatim, Figure::setVerbatim);
        binder.bind(tfPlacementNo, Figure::getPlacementNo, Figure::setPlacementNo);
        binder.bind(cbYear, Figure::getYear, Figure::setYear);
        binder.bind(cbLine, Figure::getProductLine, Figure::setProductLine);

        horizontalLayout.setSpacing(true);

        addKeyPressListener(Key.ENTER, e -> save());

        tfVerbatim.setValueChangeMode(ValueChangeMode.EAGER);
        tfVerbatim.addValueChangeListener(e -> imageSelector.updateImageGallery(e.getSource().getValue()));

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> close());
    }

    private void delete() {
        repository.delete(figure);
        figureDataProvider.refreshAll();
        close();
    }

    private void save() {
        repository.save(figure);
        figureDataProvider.refreshAll();
        close();
    }

    public final void createFigure() {
        open();
        this.figure = new Figure();
        binder.setBean(this.figure);
        tfVerbatim.focus();
    }

    public final void editFigure(Figure figure) {
        open();
        this.figure = repository.findById(figure.getId())
                .orElseThrow(() -> new IllegalStateException("Couldn't find item from list"));
        binder.setBean(this.figure);
        tfVerbatim.focus();
    }

}
