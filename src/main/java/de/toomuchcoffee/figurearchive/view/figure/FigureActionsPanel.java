package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.DataChangedEvent;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.FigureSearchEvent;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.FigureSearchResultEvent;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import de.toomuchcoffee.figurearchive.view.controls.PaginationTabs;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;
import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;
import static java.util.stream.Collectors.*;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class FigureActionsPanel extends HorizontalLayout {

    private final EventBus.SessionEventBus eventBus;
    private final FigureQueryInfo figureQueryInfo;
    private final FigureEditor figureEditor;
    private final FigureImport figureImport;
    private final FigureRepository repository;

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
            eventBus.publish(this, new FigureSearchEvent(figureFilter, 0));
        });

        cbProductLineFilter = new ComboBox<>();
        cbProductLineFilter.setPlaceholder("Filter by Product Line");
        updateProductLinesComboBox();
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

    private void updateProductLinesComboBox() {
        Map<ProductLine, Long> productLinesCount = repository.findAll().stream()
                .filter(f -> f.getProductLine() != null)
                .collect(groupingBy(Figure::getProductLine, counting()));
        List<ProductLine> productLines = Arrays.stream(ProductLine.values())
                .filter(productLinesCount::containsKey)
                .collect(toList());
        cbProductLineFilter.setItems(productLines);
        cbProductLineFilter.setItemLabelGenerator(l -> String.format("%s (%s)", l.name(), productLinesCount.get(l)));
    }

    @EventBusListenerMethod
    public void update(DataChangedEvent event) {
        tfVerbatimFilter.setValue("");
        cbProductLineFilter.setValue(null);
        updateProductLinesComboBox();
    }
}
