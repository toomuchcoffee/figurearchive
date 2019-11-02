package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entitiy.Figure;
import de.toomuchcoffee.figurearchive.entitiy.ProductLine;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class FigureFilterPanel extends HorizontalLayout {

    private final ConfigurableFilterDataProvider<Figure, Void, FigureFilter> figureDataProvider;
    private final FigureDataInfo figureDataInfo;

    @PostConstruct
    public void init() {
        FigureFilter figureFilter = new FigureFilter();

        TextField tfVerbatimFilter = new TextField("Verbatim");
        tfVerbatimFilter.setPlaceholder("Filter by verbatim");
        tfVerbatimFilter.setValueChangeMode(EAGER);
        tfVerbatimFilter.addValueChangeListener(e -> {
            figureFilter.setFilterText(e.getValue());
            figureDataProvider.setFilter(figureFilter);
        });

        ComboBox<ProductLine> cbProductLineFilter = new ComboBox<>("Product line");
        cbProductLineFilter.setItems(ProductLine.values());
        cbProductLineFilter.addValueChangeListener(e -> {
            figureFilter.setProductLine(e.getValue());
            figureDataProvider.setFilter(figureFilter);
        });

        add(tfVerbatimFilter);
        add(cbProductLineFilter);
        add(figureDataInfo);
    }
}
