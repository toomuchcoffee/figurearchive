package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService;

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

    public FigureSelector(FigureService figureService) {
        super(DEFAULT_VALUE);
        this.figureService = figureService;
    }

    @PostConstruct
    public void init() {
        FigureList availableFigures = new FigureList();
        FigureList selectedFigures = new FigureList();

        TextField tfSearchTerm = new TextField();
        tfSearchTerm.setPlaceholder("Search...");
        tfSearchTerm.setValueChangeMode(ValueChangeMode.EAGER);
        tfSearchTerm.addValueChangeListener(e -> availableFigures.update(availableFigures(e.getValue())));

        getContent().add(selectedFigures);
        getContent().add(availableFigures);

        selectedFigures.setHeader("Selected Figures");
        selectedFigures.asSingleSelect().addValueChangeListener(e ->
                Optional.ofNullable(e.getValue()).ifPresent(v -> remove(this, v)));

        availableFigures.setHeader(tfSearchTerm);
        availableFigures.asSingleSelect().addValueChangeListener(e ->
                Optional.ofNullable(e.getValue()).ifPresent(v -> add(this, v)));

        addValueChangeListener(e -> {
            selectedFigures.update(new ArrayList<>(getValue()));
            availableFigures.update(new ArrayList<>());
        });
    }

    private List<Figure> availableFigures(String query) {
        return figureService.suggestFigures(query).stream()
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
