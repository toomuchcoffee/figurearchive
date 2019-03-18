package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entitiy.Figure;
import de.toomuchcoffee.figurearchive.entitiy.ProductLine;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Route
@UIScope
public class MainView extends VerticalLayout {

    private final FigureRepository repo;

    private final Grid<Figure> grid = new Grid<>(Figure.class);
    private final TextField tfVerbatimFilter = new TextField("Verbatim");
    private ComboBox<ProductLine> cbProductLineFilter = new ComboBox<>("Product line");


    private FilterParams filterParams = new FilterParams();

    @Getter
    @Setter
    private static class FilterParams {
        private String verbatim;
        private ProductLine productLine;
    }

    public MainView(FigureRepository repo, FigureEditor editor) {
        this.repo = repo;

        Button addNewBtn = new Button("New figure", VaadinIcon.PLUS.create());

        HorizontalLayout actions = new HorizontalLayout(tfVerbatimFilter, addNewBtn);
        add(actions, grid, editor);

        grid.setHeightByRows(true);
        grid.setColumns("id", "verbatim", "productLine", "year");
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

        grid.addComponentColumn(f -> f.getImage() == null ? new Span() : new Image(f.getImage(), "n/a")).setHeader("Image");

        tfVerbatimFilter.setPlaceholder("Filter by verbatim");
        cbProductLineFilter.setItems(ProductLine.values());

        tfVerbatimFilter.setValueChangeMode(ValueChangeMode.EAGER);
        tfVerbatimFilter.addValueChangeListener(e -> {
            filterParams.setVerbatim(e.getValue());
            listFigures();
        });

        cbProductLineFilter.addValueChangeListener(e -> {
            filterParams.setProductLine(e.getValue());
            listFigures();
        });

        HorizontalLayout filter = new HorizontalLayout();
        filter.add(tfVerbatimFilter);
        filter.add(cbProductLineFilter);

        add(filter, grid);

        grid.asSingleSelect().addValueChangeListener(e -> Optional.ofNullable(e.getValue()).ifPresent(editor::editFigure));

        addNewBtn.addClickListener(e -> editor.createFigure());

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listFigures();
        });

        listFigures();
    }

    private void listFigures() {
        if (StringUtils.isEmpty(filterParams.getVerbatim()) && filterParams.getProductLine() == null) {
            grid.setItems(repo.findAll());
        } else if (StringUtils.isEmpty(filterParams.getVerbatim()) && filterParams.getProductLine() != null) {
            grid.setItems(repo.findByProductLine(filterParams.getProductLine()));
        } else if (!StringUtils.isEmpty(filterParams.getVerbatim()) && filterParams.getProductLine() == null) {
            grid.setItems(repo.findByVerbatimStartsWithIgnoreCase(filterParams.getVerbatim()));
        } else {
            grid.setItems(repo.findByVerbatimStartsWithIgnoreCaseAndProductLine(filterParams.getVerbatim(), filterParams.getProductLine()));
        }

    }
}
