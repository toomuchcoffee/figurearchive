package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import de.toomuchcoffee.figurearchive.entitiy.Figure;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Route
public class MainView extends VerticalLayout {

    private final FigureRepository repo;

    private final Grid<Figure> grid;
    private final TextField filter;

    public MainView(FigureRepository repo, FigureEditor editor) {
        this.repo = repo;

        this.grid = new Grid<>(Figure.class);
        this.filter = new TextField();
        Button addNewBtn = new Button("New figure", VaadinIcon.PLUS.create());

        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        add(actions, grid, editor);

        grid.setHeightByRows(true);
        grid.setColumns("id", "verbatim", "productLine", "year");
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

        grid.addComponentColumn(f -> f.getImage() == null ? new Span() : new Image(f.getImage(), "n/a")).setHeader("Image");

        filter.setPlaceholder("Filter by verbatim");

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> listFigures(e.getValue()));
        add(filter, grid);

        grid.asSingleSelect().addValueChangeListener(e -> Optional.ofNullable(e.getValue()).ifPresent(editor::editFigure));

        addNewBtn.addClickListener(e -> editor.createFigure());

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listFigures(filter.getValue());
        });

        listFigures(null);
    }

    private void listFigures(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repo.findAll());
        } else {
            grid.setItems(repo.findByVerbatimStartsWithIgnoreCase(filterText));
        }

    }
}
