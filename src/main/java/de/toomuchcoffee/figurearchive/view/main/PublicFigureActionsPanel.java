package de.toomuchcoffee.figurearchive.view.main;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.event.FigureImportEvent;
import de.toomuchcoffee.figurearchive.event.FigureSearchEvent;
import de.toomuchcoffee.figurearchive.event.FigureSearchResultEvent;
import de.toomuchcoffee.figurearchive.service.FigureService;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import de.toomuchcoffee.figurearchive.view.controls.PaginationTabs;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.vaadin.flow.component.orderedlayout.FlexLayout.WrapMode.WRAP;
import static com.vaadin.flow.data.value.ValueChangeMode.LAZY;
import static java.util.stream.Collectors.toList;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class PublicFigureActionsPanel extends FlexLayout {

    private final EventBus.UIEventBus eventBus;
    private final FigureService figureService;

    private ComboBox<ProductLine> cbProductLineFilter;
    private Map<ProductLine, Long> productLinesCount;
    private TextField tfVerbatimFilter;

    @PostConstruct
    public void init() {
        setWrapMode(WRAP);
        FigureFilter figureFilter = new FigureFilter();

        tfVerbatimFilter = new TextField();
        tfVerbatimFilter.setPlaceholder("Filter by Verbatim");
        tfVerbatimFilter.setClearButtonVisible(true);
        tfVerbatimFilter.setValueChangeMode(LAZY);
        tfVerbatimFilter.setValueChangeTimeout(500);
        tfVerbatimFilter.addValueChangeListener(e -> {
            figureFilter.setFilterText(e.getValue());
            eventBus.publish(this, new FigureSearchEvent(figureFilter, 0));
        });

        cbProductLineFilter = new ComboBox<>();
        cbProductLineFilter.setClearButtonVisible(true);
        cbProductLineFilter.setPlaceholder("Filter by Product Line");
        cbProductLineFilter.addValueChangeListener(e -> {
            figureFilter.setProductLine(e.getValue());
            eventBus.publish(this, new FigureSearchEvent(figureFilter, 0));
        });

        PaginationTabs pagination = new PaginationTabs<FigureSearchResultEvent, FigureSearchEvent, FigureFilter>(
                eventBus, FigureSearchResultEvent.class, FigureSearchEvent.class);

        add(tfVerbatimFilter, cbProductLineFilter, pagination);

        addAttachListener(e -> {
            productLinesCount = figureService.getProductLineInfo();
            cbProductLineFilter.setItems(Arrays.stream(ProductLine.values()).collect(toList()));
            cbProductLineFilter.setItemLabelGenerator(l -> String.format("%s (%s)", l.name(), productLinesCount.getOrDefault(l, 0L)));
            eventBus.subscribe(this);
            eventBus.publish(this, new FigureSearchEvent(figureFilter, 0));
        });
        addDetachListener(e -> eventBus.unsubscribe(this));
    }

    @EventBusListenerMethod
    public void update(FigureImportEvent event) {
        productLinesCount = figureService.getProductLineInfo();
        cbProductLineFilter.getDataProvider().refreshAll();
        Optional.ofNullable(event.getValue().get(0))
                .ifPresent(figure -> {
                    cbProductLineFilter.setValue(figure.getProductLine());
                    tfVerbatimFilter.setValue("");
                });
    }
}
