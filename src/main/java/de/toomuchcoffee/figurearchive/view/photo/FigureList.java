package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.toomuchcoffee.figurearchive.entity.Figure;

import java.util.Collection;
import java.util.List;

import static de.toomuchcoffee.figurearchive.util.FigureDisplayNameHelper.getDisplayName;

class FigureList extends Grid<Figure> {

    FigureList() {
        super(Figure.class);
        setWidth("500px");
        setHeight("250px");

        setColumns();

        addComponentColumn(f -> new Span(getDisplayName(f)));
    }

    void setHeader(Component component) {
        getColumns().get(0).setHeader(component);
    }

    void update(List<Figure> figures) {
        this.setItems(figures);
    }

    @SuppressWarnings("unchecked")
    public Collection<Figure> getItems() {
        return ((ListDataProvider<Figure>)getDataProvider()).getItems();
    }
}
