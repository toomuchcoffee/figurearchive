package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.DataChangedEvent;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoSearchByVerbatimEvent;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;
import java.util.stream.IntStream;

import static com.vaadin.flow.component.icon.VaadinIcon.*;
import static de.toomuchcoffee.figurearchive.config.EventBusConfig.DataChangedEvent.Operation.*;
import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

@SpringComponent
@UIScope
@RequiredArgsConstructor
public class FigureEditor extends Dialog implements KeyNotifier {

    private final FigureRepository repository;
    private final PhotoSelector photoSelector;
    private final EventBus.SessionEventBus eventBus;

    private Figure figure;

    private TextField tfVerbatim = new TextField("Verbatim");
    private TextField tfPlacementNo = new TextField("Placement No.");
    private ComboBox<Short> cbYear = new ComboBox<>("Year");
    private ComboBox<ProductLine> cbLine = new ComboBox<>("Line");

    private Button save = new Button("Save", CHECK.create(), e -> save());
    private Button cancel = new Button("Cancel", EXIT.create(), e -> resetAndClose());
    private Button delete = new Button("Delete", TRASH.create(), e -> delete());
    private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private Binder<Figure> binder;

    @PostConstruct
    public void init() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        add(horizontalLayout);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(tfVerbatim, cbLine, cbYear, tfPlacementNo, actions);
        horizontalLayout.add(verticalLayout, photoSelector);

        cbYear.setItems(IntStream.range(1977, now().getYear() + 1).mapToObj(value -> (short) value).collect(toList()));
        cbLine.setItems(ProductLine.values());
        cbLine.setItemLabelGenerator(ProductLine::name);

        binder = new Binder<>();
        binder.bind(photoSelector, Figure::getPhotos, Figure::setPhotos);
        binder.bind(tfVerbatim, Figure::getVerbatim, Figure::setVerbatim);
        binder.bind(tfPlacementNo, Figure::getPlacementNo, Figure::setPlacementNo);
        binder.bind(cbYear, Figure::getYear, Figure::setYear);
        binder.bind(cbLine, Figure::getProductLine, Figure::setProductLine);

        horizontalLayout.setSpacing(true);

        addKeyPressListener(Key.ENTER, e -> save());

        tfVerbatim.setValueChangeMode(ValueChangeMode.EAGER);
        tfVerbatim.addValueChangeListener(e -> eventBus
                .publish(this, new PhotoSearchByVerbatimEvent(e.getSource().getValue())));
    }

    private void delete() {
        repository.delete(figure);
        eventBus.publish(this, new DataChangedEvent<>(figure, DELETED));

        resetAndClose();
    }

    private void save() {
        boolean isNew = figure.getId() == null;
        repository.save(figure);
        eventBus.publish(this, new DataChangedEvent<>(figure, isNew ? CREATED : UPDATED));

        resetAndClose();
    }

    private void resetAndClose() {
        this.figure = null;
        this.binder.removeBean();
        close();
    }

    public final void createFigure() {
        open();
        this.figure = new Figure();
        binder.setBean(this.figure);
        tfVerbatim.focus();
    }

    final void editFigure(Figure figure) {
        open();
        this.figure = figure;
        binder.setBean(this.figure);
        tfVerbatim.focus();
    }

}
