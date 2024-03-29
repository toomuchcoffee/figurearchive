package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.event.FigureChangedEvent;
import de.toomuchcoffee.figurearchive.service.FigureService;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import de.toomuchcoffee.figurearchive.view.controls.ScrollableLayout;
import de.toomuchcoffee.figurearchive.view.photo.FigureList;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static com.vaadin.flow.component.icon.VaadinIcon.*;
import static com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap.WRAP;
import static de.toomuchcoffee.figurearchive.event.EntityChangedEvent.Operation.*;
import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

@SpringComponent
@UIScope
@RequiredArgsConstructor
public class FigureEditor extends Dialog implements KeyNotifier {

    private final FigureService figureService;
    private final PhotoService photoService;
    private final EventBus.UIEventBus eventBus;

    private Figure figure;

    private TextField tfVerbatim;

    private final Button save = new Button("Save", CHECK.create(), e -> save());
    private final Button cancel = new Button("Cancel", EXIT.create(), e -> close());
    private final Button delete = new Button("Delete", TRASH.create(), e -> delete());
    private FormLayout actions;
    private ComboBox<String> cbLine;
    private ComboBox<Short> cbYear;
    private TextField tfPlacementNo;
    private TextField tfCount;


    private FigureList similarFigures;
    private PhotoGallery photoGallery;

    private Binder<Figure> binder;

    @PostConstruct
    public void init() {
        FlexLayout wrapper = new FlexLayout();
        wrapper.setFlexWrap(WRAP);
        add(wrapper);

        Div attributes = new Div();
        FormLayout form = new FormLayout();
        attributes.add(form);
        attributes.setWidth("282px");

        tfVerbatim = new TextField(null, "Verbatim");
        cbLine = new ComboBox<>();
        cbYear = new ComboBox<>();
        tfPlacementNo = new TextField(null, "Placement No.");
        tfCount = new TextField(null, "count");
        tfCount.setWidth("20%");
        tfCount.setEnabled(false);
        HorizontalLayout count = new HorizontalLayout(
                new Button(ARROW_CIRCLE_LEFT.create(), e -> decreaseCount(tfCount)),
                tfCount,
                new Button(ARROW_CIRCLE_RIGHT.create(), e -> increaseCount(tfCount)));
        form.add(tfVerbatim, cbLine, cbYear, tfPlacementNo, count);

        VerticalLayout context = new ScrollableLayout();
        context.setWidth("282px");

        actions = new FormLayout();
        wrapper.add(attributes, context, actions);

        similarFigures = new FigureList();
        similarFigures.setHeader(new Span("Similar Existing Figures"));
        similarFigures.setVisible(false);
        context.add(similarFigures);

        tfVerbatim.setValueChangeMode(ValueChangeMode.EAGER);
        tfVerbatim.addValueChangeListener(e -> similarFigures.update(isBlank(e.getValue()) ? newArrayList() : similarFigures(e.getValue())));

        photoGallery = new PhotoGallery(photoService);
        photoGallery.setVisible(false);
        context.add(photoGallery);

        actions.add(save, cancel);

        cbYear.setPlaceholder("Year");
        cbYear.setItems(IntStream.range(1977, now().getYear() + 1).mapToObj(value -> (short) value).collect(toList()));
        cbYear.setClearButtonVisible(true);
        cbLine.setPlaceholder("Product Line");
        cbLine.setItems(figureService.getProductLines());
        cbLine.setItemLabelGenerator(String::toString);
        cbLine.setClearButtonVisible(true);

        binder = new Binder<>();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        cbLine.setItems(figureService.getProductLines());

        binder.forField(tfVerbatim)
                .asRequired()
                .bind(Figure::getVerbatim, Figure::setVerbatim);
        binder.forField(cbLine)
                .asRequired()
                .bind(Figure::getProductLine, Figure::setProductLine);
        binder.bind(cbYear, Figure::getYear, Figure::setYear);
        binder.bind(tfPlacementNo, Figure::getPlacementNo, Figure::setPlacementNo);
        binder.forField(tfCount)
                .withConverter(new StringToIntegerConverter("Not a number!"))
                .bind(Figure::getCount, Figure::setCount);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        this.figure = null;
        binder.removeBean();
        similarFigures.setVisible(false);
        photoGallery.setVisible(false);
        tfVerbatim.setValue("");

        super.onDetach(detachEvent);
    }

    private void increaseCount(TextField tf) {
        int current = Integer.parseInt(tf.getValue());
        tf.setValue(String.valueOf(current + 1));
    }

    private void decreaseCount(TextField tf) {
        int current = Integer.parseInt(tf.getValue());
        if (current > 0) {
            tf.setValue(String.valueOf(current - 1));
        }
    }

    private List<Figure> similarFigures(String query) {
        return figureService.fuzzySearch(query).stream()
                .limit(50)
                .collect(toList());
    }

    private void delete() {
        figureService.delete(figure);
        eventBus.publish(this, new FigureChangedEvent(figure, DELETED));
        close();
    }

    private void save() {
        binder.validate();
        if (binder.isValid()) {
            boolean isNew = figure.getId() == null;
            figureService.save(figure);
            eventBus.publish(this, new FigureChangedEvent(figure, isNew ? CREATED : UPDATED));
            close();
        }
    }

    public final void createFigure() {
        open();
        this.figure = new Figure();
        binder.setBean(this.figure);
        similarFigures.setVisible(true);
        tfVerbatim.focus();
    }

    final void editFigure(Figure figure) {
        open();
        this.figure = figure;
        binder.setBean(this.figure);
        photoGallery.update(figure);
        photoGallery.setVisible(true);
        actions.add(save, cancel, delete);
        tfVerbatim.focus();
    }

}
