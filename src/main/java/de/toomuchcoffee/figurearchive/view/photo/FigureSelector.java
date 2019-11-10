package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;

import javax.annotation.PostConstruct;
import java.util.*;

import static de.toomuchcoffee.figurearchive.util.ValueSetHelper.add;
import static de.toomuchcoffee.figurearchive.util.ValueSetHelper.remove;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@UIScope
@SpringComponent
@Tag("div")
public class FigureSelector extends AbstractCompositeField<HorizontalLayout, FigureSelector, Set<Figure>> {
    private final FigureService figureService;

    private static final Set<Figure> DEFAULT_VALUE = new HashSet<>();

    private FigureList availableFigures = new FigureList();
    private FigureList selectedFigures = new FigureList();

    public FigureSelector(FigureService figureService) {
        super(DEFAULT_VALUE);
        this.figureService = figureService;
    }

    @PostConstruct
    public void init() {

        TextField tfSearchTerm = new TextField();
        tfSearchTerm.setPlaceholder("Search by Tag");
        tfSearchTerm.setValueChangeMode(ValueChangeMode.EAGER);
        tfSearchTerm.addValueChangeListener(e -> availableFigures.update(availableFigures(new FigureFilter(e.getValue(), null))));

        getContent().add(new VerticalLayout(new Label("Selected Figures"), selectedFigures));
        getContent().add(new VerticalLayout(tfSearchTerm, availableFigures));

        availableFigures.asSingleSelect().addValueChangeListener(e ->
                Optional.ofNullable(e.getValue()).ifPresent(v -> add(this, v)));

        selectedFigures.asSingleSelect().addValueChangeListener(e ->
                Optional.ofNullable(e.getValue()).ifPresent(v -> remove(this, v)));

        addValueChangeListener(e -> {
            selectedFigures.update(new ArrayList<>(getValue()));
            availableFigures.update(availableFigures(null));
        });
    }

    private List<Figure> availableFigures(FigureFilter query) {
        return figureService.findFigures(query).stream()
                .filter(this::isSelected)
                .limit(50)
                .collect(toList());
    }

    private boolean isSelected(Figure figure) {
        return !getValue().stream().map(Figure::getId).collect(toSet()).contains(figure.getId());
    }

    @Override
    protected void setPresentationValue(Set<Figure> s) {
    }

}
