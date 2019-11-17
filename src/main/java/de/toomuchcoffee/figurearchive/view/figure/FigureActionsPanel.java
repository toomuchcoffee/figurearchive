package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.EventBusConfig;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.FigureSearchEvent;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.FigureSearchResultEvent;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import de.toomuchcoffee.figurearchive.view.controls.PaginationTabs;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;
import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class FigureActionsPanel extends HorizontalLayout {

    private final EventBus.SessionEventBus eventBus;
    private final FigureQueryInfo figureQueryInfo;
    private final FigureEditor figureEditor;
    private final FigureImport figureImport;

    private TextField tfVerbatimFilter;
    private ComboBox<ProductLine> cbProductLineFilter;

    @PostConstruct
    public void init() {
        FigureFilter figureFilter = new FigureFilter();

        tfVerbatimFilter = new TextField();
        tfVerbatimFilter.setPlaceholder("Filter by Verbatim");
        tfVerbatimFilter.setValueChangeMode(EAGER);
        tfVerbatimFilter.addValueChangeListener(e -> {
            figureFilter.setFilterText(e.getValue());
            if (e.getValue().length() >= 2) {
                eventBus.publish(this, new FigureSearchEvent(figureFilter, 0));
            }
        });

        cbProductLineFilter = new ComboBox<>();
        cbProductLineFilter.setPlaceholder("Filter by Product Line");
        cbProductLineFilter.setItems(ProductLine.values());
        cbProductLineFilter.addValueChangeListener(e -> {
            figureFilter.setProductLine(e.getValue());
            eventBus.publish(this, new FigureSearchEvent(figureFilter, 0));
        });

        PaginationTabs pagination = new PaginationTabs<FigureSearchResultEvent, FigureSearchEvent, FigureFilter>(
                eventBus, FigureSearchResultEvent.class, FigureSearchEvent.class);

        Button newFigureButton = new Button("New Figure", PLUS.create(), e -> figureEditor.createFigure());

        add(tfVerbatimFilter, cbProductLineFilter, pagination, figureQueryInfo, newFigureButton, figureImport);

        eventBus.subscribe(this);
    }

    @EventBusListenerMethod
    public void update(EventBusConfig.DataChangedEvent event) {
        tfVerbatimFilter.setValue("");
        cbProductLineFilter.setValue(null);
    }
}
