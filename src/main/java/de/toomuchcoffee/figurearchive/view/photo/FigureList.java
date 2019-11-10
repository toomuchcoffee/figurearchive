package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.grid.Grid;
import de.toomuchcoffee.figurearchive.entity.Figure;

import java.util.List;

public class FigureList extends Grid<Figure> {

    public FigureList() {
        super(Figure.class);
        setWidth("200px");
        setHeight("200px");

        setColumns("verbatim");
    }

    public void update(List<Figure> figures) {
        this.setItems(figures);
    }
}
