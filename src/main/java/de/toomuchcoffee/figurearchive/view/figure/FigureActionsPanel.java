package de.toomuchcoffee.figurearchive.view.figure;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.FigureSearchEvent;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import de.toomuchcoffee.figurearchive.view.controls.NewFigureButton;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class FigureActionsPanel extends HorizontalLayout {

    private final EventBus.SessionEventBus eventBus;
    private final FigureDataInfo figureDataInfo;
    private final NewFigureButton newFigureButton;
    private final FigureImport figureImport;

    @PostConstruct
    public void init() {
        FigureFilter figureFilter = new FigureFilter();

        TextField tfVerbatimFilter = new TextField();
        tfVerbatimFilter.setPlaceholder("Filter by Verbatim");
        tfVerbatimFilter.setValueChangeMode(EAGER);
        tfVerbatimFilter.addValueChangeListener(e -> {
            figureFilter.setFilterText(e.getValue());
            eventBus.publish(this, new FigureSearchEvent(figureFilter));
        });

        ComboBox<ProductLine> cbProductLineFilter = new ComboBox<>();
        cbProductLineFilter.setPlaceholder("Filter by Product Line");
        cbProductLineFilter.setItems(ProductLine.values());
        cbProductLineFilter.addValueChangeListener(e -> {
            figureFilter.setProductLine(e.getValue());
            eventBus.publish(this, new FigureSearchEvent(figureFilter));
        });

        add(tfVerbatimFilter, cbProductLineFilter, figureDataInfo, newFigureButton, figureImport);
    }
}
