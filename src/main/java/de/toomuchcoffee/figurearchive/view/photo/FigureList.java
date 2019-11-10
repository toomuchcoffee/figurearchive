package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import de.toomuchcoffee.figurearchive.entity.Figure;

import java.util.ArrayList;
import java.util.List;

public class FigureList extends Grid<Figure> {

    private final List<Figure> figures = new ArrayList<>();
    private final CallbackDataProvider<Figure, Void> dataProvider;

    public FigureList() {
        super(Figure.class);
        setWidth("200px");
        setHeight("200px");

        setColumns("verbatim");
        dataProvider = DataProvider.fromCallbacks(
                query -> {
                    query.getLimit();
                    query.getOffset();
                    return figures.stream();
                },
                query -> figures.size()
        );
        setDataProvider(dataProvider);
    }

    public void update(List<Figure> figures) {
        this.figures.clear();
        this.figures.addAll(figures);
        this.dataProvider.refreshAll();
    }
}
