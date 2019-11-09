package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import de.toomuchcoffee.figurearchive.view.controls.CsvUploader;
import de.toomuchcoffee.figurearchive.view.controls.NewFigureButton;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.data.value.ValueChangeMode.EAGER;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class FigureActionsPanel extends HorizontalLayout {

    private final ConfigurableFilterDataProvider<Figure, Void, FigureFilter> figureDataProvider;
    private final FigureDataInfo figureDataInfo;
    private final NewFigureButton newFigureButton;
    private final CsvUploader csvUploader;

    @PostConstruct
    public void init() {
        FigureFilter figureFilter = new FigureFilter();

        TextField tfVerbatimFilter = new TextField();
        tfVerbatimFilter.setPlaceholder("Filter by Verbatim");
        tfVerbatimFilter.setValueChangeMode(EAGER);
        tfVerbatimFilter.addValueChangeListener(e -> {
            figureFilter.setFilterText(e.getValue());
            figureDataProvider.setFilter(figureFilter);
        });

        ComboBox<ProductLine> cbProductLineFilter = new ComboBox<>();
        cbProductLineFilter.setPlaceholder("Flter by Product Line");
        cbProductLineFilter.setItems(ProductLine.values());
        cbProductLineFilter.addValueChangeListener(e -> {
            figureFilter.setProductLine(e.getValue());
            figureDataProvider.setFilter(figureFilter);
        });

        add(tfVerbatimFilter, cbProductLineFilter, figureDataInfo, newFigureButton, csvUploader);
    }
}
