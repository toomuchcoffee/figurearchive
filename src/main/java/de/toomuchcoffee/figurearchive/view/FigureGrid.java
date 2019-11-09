package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;

import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;

public class FigureGrid extends Grid<Figure> {

    public FigureGrid(
            ConfigurableFilterDataProvider<Figure, Void, FigureFilter> figureDataProvider,
            ValueChangeListener<ValueChangeEvent<Figure>> valueChangeListener) {
        super(Figure.class);
        asSingleSelect().addValueChangeListener(valueChangeListener);
        setDataProvider(figureDataProvider);
        setPageSize(1000); // TODO
        setColumns("placementNo", "verbatim", "productLine", "year");
        getColumnByKey("placementNo").setWidth("150px").setFlexGrow(0);
        addComponentColumn(f -> f.getPhotos().isEmpty() ? new Span() : new Image(getImageUrl(f.getPhotos().iterator().next(), 75), "n/a")).setHeader("Image");
    }

}
