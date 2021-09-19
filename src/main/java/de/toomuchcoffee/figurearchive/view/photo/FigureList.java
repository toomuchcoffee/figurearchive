package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.toomuchcoffee.figurearchive.entity.Figure;

import java.util.Collection;
import java.util.List;

import static de.toomuchcoffee.figurearchive.util.FigureDisplayNameHelper.getDisplayName;

public class FigureList extends Grid<Figure> {

    public FigureList() {
        super(Figure.class);
        setWidthFull();
        setHeightFull();
        setMinHeight("250px");
        setColumns();

        addComponentColumn(f -> new Span(getDisplayName(f)));
    }

    public void setHeader(Component component) {
        getColumns().get(0).setHeader(component);
    }

    public void update(List<Figure> figures) {
        this.setItems(figures);
        getElement().callJsFunction("_scrollToIndex", 0);
    }

    @SuppressWarnings("unchecked")
    Collection<Figure> getItems() {
        return ((ListDataProvider<Figure>) getDataProvider()).getItems();
    }
}
