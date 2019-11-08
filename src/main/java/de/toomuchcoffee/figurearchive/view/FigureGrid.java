package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;

import javax.annotation.PostConstruct;

import static de.toomuchcoffee.figurearchive.util.PhotoUrlHelper.getImageUrl;

@SpringComponent
@UIScope
public class FigureGrid extends Grid<Figure> {

    private final ConfigurableFilterDataProvider<Figure, Void, FigureFilter> figureDataProvider;

    public FigureGrid(ConfigurableFilterDataProvider<Figure, Void, FigureFilter> figureDataProvider) {
        super(Figure.class);

        this.figureDataProvider = figureDataProvider;
    }

    @PostConstruct
    public void init() {
        setDataProvider(figureDataProvider);
        setPageSize(1000); // TODO
        setHeightByRows(true);
        setColumns("placementNo", "verbatim", "productLine", "year");
        getColumnByKey("placementNo").setWidth("150px").setFlexGrow(0);
        addComponentColumn(f -> f.getPhotos().isEmpty() ? new Span() : new Image(getImageUrl(f.getPhotos().iterator().next(), 75), "n/a")).setHeader("Image");
    }

}
