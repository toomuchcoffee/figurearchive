package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entitiy.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;

import javax.annotation.PostConstruct;
import java.util.Optional;

@SpringComponent
@UIScope
public class FigureGrid extends Grid<Figure> {

    private final ConfigurableFilterDataProvider<Figure, Void, FigureFilter> figureDataProvider;
    private final FigureEditor figureEditor;

    public FigureGrid(ConfigurableFilterDataProvider<Figure, Void, FigureFilter> figureDataProvider, FigureEditor figureEditor) {
        super(Figure.class);

        this.figureDataProvider = figureDataProvider;
        this.figureEditor = figureEditor;
    }

    @PostConstruct
    public void init() {
        setDataProvider(figureDataProvider);
        setPageSize(1000); // TODO
        setHeightByRows(true);
        setColumns("placementNo", "verbatim", "productLine", "year");
        getColumnByKey("placementNo").setWidth("150px").setFlexGrow(0);
        addComponentColumn(f -> f.getImage() == null ? new Span() : new Image(f.getImage(), "n/a")).setHeader("Image");
        asSingleSelect().addValueChangeListener(e -> Optional.ofNullable(e.getValue()).ifPresent(figureEditor::editFigure));
    }

}
