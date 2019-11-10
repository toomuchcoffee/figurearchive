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

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        tfSearchTerm.addValueChangeListener(e -> availableFigures.update(availableFigures(e.getValue())));

        getContent().add(new VerticalLayout(new Label("Selected Figures"), selectedFigures));
        getContent().add(new VerticalLayout(tfSearchTerm, availableFigures));

        availableFigures.asSingleSelect().addValueChangeListener(e -> add(this, e.getValue()));

        selectedFigures.asSingleSelect().addValueChangeListener(e -> remove(this, e.getValue()));

        addValueChangeListener(e -> {
            selectedFigures.update(new ArrayList<>(getValue()));
            availableFigures.update(availableFigures(null));
        });
    }

    private List<Figure> availableFigures(String query) {
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
