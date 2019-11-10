package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import de.toomuchcoffee.figurearchive.entity.Figure;

import java.util.List;

import static de.toomuchcoffee.figurearchive.util.FigureDisplayNameHelper.getDisplayName;

public class FigureList extends Grid<Figure> {

    FigureList() {
        super(Figure.class);
        setWidth("240px");
        setHeight("200px");

        setColumns();

        addComponentColumn(f -> new Span(getDisplayName(f)));
    }

    public void setHeader(String labelText) {
        getColumns().get(0).setHeader(labelText);
    }

    public void setHeader( Component component) {
        getColumns().get(0).setHeader(component);
    }

    void update(List<Figure> figures) {
        this.setItems(figures);
    }
}
