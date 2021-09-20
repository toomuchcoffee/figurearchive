package de.toomuchcoffee.figurearchive.view.special;

import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.event.ProductLineChangedEvent;
import de.toomuchcoffee.figurearchive.service.ProductLineService;
import de.toomuchcoffee.figurearchive.view.controls.ScrollableLayout;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;
import java.util.stream.IntStream;

import static com.vaadin.flow.component.icon.VaadinIcon.*;
import static com.vaadin.flow.component.orderedlayout.FlexLayout.WrapMode.WRAP;
import static de.toomuchcoffee.figurearchive.event.EntityChangedEvent.Operation.DELETED;
import static de.toomuchcoffee.figurearchive.event.EntityChangedEvent.Operation.UPDATED;
import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

@SpringComponent
@UIScope
@RequiredArgsConstructor
public class ProductLineEditor extends Dialog implements KeyNotifier {

    private final ProductLineService productLineService;
    private final EventBus.UIEventBus eventBus;

    private VerticalLayout context;

    private final Button save = new Button("Save", CHECK.create(), e -> save());
    private final Button cancel = new Button("Cancel", EXIT.create(), e -> close());
    private final Button delete = new Button("Delete", TRASH.create(), e -> delete());
    private FormLayout actions;

    private ProductLine productLine;

    private Binder<ProductLine> binder;

    @PostConstruct
    public void init() {
        FlexLayout wrapper = new FlexLayout();
        wrapper.setWrapMode(WRAP);
        add(wrapper);

        Div attributes = new Div();
        FormLayout form = new FormLayout();
        attributes.add(form);
        attributes.setWidth("282px");

        TextField tfCode = new TextField(null, "Code");
        TextField tfDescription = new TextField(null, "Description");
        ComboBox<Short> cbYear = new ComboBox<>();
        form.add(tfCode, tfDescription, cbYear);

        context = new ScrollableLayout();
        context.setWidth("282px");

        actions = new FormLayout();
        wrapper.add(attributes, context, actions);

        tfCode.setValueChangeMode(ValueChangeMode.EAGER);

        cbYear.setPlaceholder("Year");
        cbYear.setItems(IntStream.range(1977, now().getYear() + 1).mapToObj(value -> (short) value).collect(toList()));
        cbYear.setClearButtonVisible(true);

        binder = new Binder<>();
        binder.forField(tfCode)
                .asRequired()
                .bind(ProductLine::getCode, ProductLine::setCode);
        binder.forField(tfDescription)
                .asRequired()
                .bind(ProductLine::getDescription, ProductLine::setDescription);
        binder.bind(cbYear, ProductLine::getYear, ProductLine::setYear);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        this.productLine = null;
        binder.removeBean();
        context.removeAll();

        super.onDetach(detachEvent);
    }

    private void delete() {
        productLineService.delete(productLine);
        eventBus.publish(this, new ProductLineChangedEvent(productLine, DELETED));
        close();
    }

    private void save() {
        binder.validate();
        if (binder.isValid()) {
            productLineService.save(productLine);
            eventBus.publish(this, new ProductLineChangedEvent(productLine, UPDATED));
            close();
        }
    }

    public final void createProductLine() {
        open();
        this.productLine = new ProductLine();
        binder.setBean(this.productLine);
        actions.add(save, cancel);
    }

    final void editProductLine(ProductLine productLine) {
        open();
        this.productLine = productLine;
        delete.setEnabled(!productLineService.isUsed(productLine.getCode()));
        binder.setBean(this.productLine);
        actions.add(save, cancel, delete);
    }

}
