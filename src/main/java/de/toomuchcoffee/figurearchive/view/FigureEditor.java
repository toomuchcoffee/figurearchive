package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entitiy.Figure;
import de.toomuchcoffee.figurearchive.entitiy.ProductLine;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.IntStream;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

@SpringComponent
@UIScope
public class FigureEditor extends HorizontalLayout implements KeyNotifier {

    private final FigureRepository repository;

    private Figure figure;

    private TextField tfVerbatim = new TextField("Verbatim");
    private TextField tfPlacementNo = new TextField("Placement No.");
    private ComboBox<Integer> cbYear = new ComboBox<>("Year");
    private ComboBox<ProductLine> cbLine = new ComboBox<>("Line");

    private Button save = new Button("Save", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private Binder<Figure> binder;
    private ChangeHandler changeHandler;

    @Autowired
    public FigureEditor(FigureRepository repository, ImageSelector imageSelector) {
        this.repository = repository;

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(tfVerbatim, cbLine, cbYear, tfPlacementNo, actions);
        add(verticalLayout, imageSelector);

        cbYear.setItems(IntStream.range(1977, now().getYear()).boxed().collect(toList()));
        cbLine.setItems(ProductLine.values());
        cbLine.setItemLabelGenerator(ProductLine::name);

        binder = new Binder<>();
        binder.bind(imageSelector, Figure::getImage, Figure::setImage);
        binder.bind(tfVerbatim, Figure::getVerbatim, Figure::setVerbatim);
        binder.bind(tfPlacementNo, Figure::getPlacementNo, Figure::setPlacementNo);
        binder.bind(cbYear, Figure::getYear, Figure::setYear);
        binder.bind(cbLine, Figure::getProductLine, Figure::setProductLine);

        setSpacing(true);

        addKeyPressListener(Key.ENTER, e -> save());

        tfVerbatim.setValueChangeMode(ValueChangeMode.EAGER);
        tfVerbatim.addValueChangeListener(e -> imageSelector.updateImageGallery(e.getSource().getValue()));

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> cancel());
        setVisible(false);
    }

    private void delete() {
        repository.delete(figure);
        changeHandler.onChange();
    }

    private void save() {
        repository.save(figure);
        changeHandler.onChange();
    }

    public final void cancel() {
        changeHandler.onChange();
    }

    public final void createFigure() {
        this.figure = new Figure();

        binder.setBean(this.figure);

        setVisible(true);

        tfVerbatim.focus();
    }

    public final void editFigure(Figure figure) {
        this.figure = repository.findById(figure.getId())
                .orElseThrow(() -> new IllegalStateException("Couldn't find item from list"));

        binder.setBean(this.figure);

        setVisible(true);

        tfVerbatim.focus();
    }

    public interface ChangeHandler {
        void onChange();

    }

    public void setChangeHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

}
