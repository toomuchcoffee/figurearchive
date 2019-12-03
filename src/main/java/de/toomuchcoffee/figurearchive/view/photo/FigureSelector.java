package de.toomuchcoffee.figurearchive.view.photo;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Label;
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

import static com.google.common.collect.Lists.newArrayList;
import static de.toomuchcoffee.figurearchive.util.FigureDisplayNameHelper.getDisplayName;
import static de.toomuchcoffee.figurearchive.util.ValueSetHelper.add;
import static de.toomuchcoffee.figurearchive.util.ValueSetHelper.remove;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.jsoup.helper.StringUtil.isBlank;

@UIScope
@SpringComponent
@Tag("div")
public class FigureSelector extends AbstractCompositeField<VerticalLayout, FigureSelector, Set<Figure>> {
    private final FigureService figureService;

    private static final Set<Figure> DEFAULT_VALUE = new HashSet<>();

    public FigureSelector(FigureService figureService) {
        super(DEFAULT_VALUE);
        this.figureService = figureService;
    }

    @PostConstruct
    public void init() {
        getContent().setHeightFull();
        getContent().setWidth("282px");
        FigureList availableFigures = new FigureList();
        FigureList selectedFigures = new FigureList();

        TextField tfSearchTerm = new TextField();
        tfSearchTerm.setPlaceholder("Search...");
        tfSearchTerm.setClearButtonVisible(true);
        tfSearchTerm.setValueChangeMode(ValueChangeMode.EAGER);
        tfSearchTerm.addValueChangeListener(e -> availableFigures.update(isBlank(e.getValue()) ? newArrayList() : availableFigures(e.getValue())));

        getContent().add(selectedFigures);
        getContent().add(availableFigures);

        selectedFigures.setHeader(new Label("Selected Figures"));
        selectedFigures.addItemClickListener(e -> {
            remove(this, e.getItem());
            FigureSelectionNotification.show("removed", getDisplayName(e.getItem()), getValue().size());
        });

        availableFigures.setHeader(tfSearchTerm);
        availableFigures.addItemClickListener(e -> {
            add(this, e.getItem());
            FigureSelectionNotification.show("added", getDisplayName(e.getItem()), getValue().size());
        });

        addValueChangeListener(e -> {
            selectedFigures.update(new ArrayList<>(getValue()));
            List<Figure> availableFigureList = availableFigures.getItems().stream()
                    .filter(this::isNotSelected)
                    .collect(toList());
            availableFigures.update(availableFigureList);
        });

        addDetachListener(e -> {
            availableFigures.update(new ArrayList<>());
            tfSearchTerm.setValue("");
        });
    }

    private List<Figure> availableFigures(String query) {
        return figureService.fuzzySearch(query).stream()
                .filter(this::isNotSelected)
                .limit(50)
                .collect(toList());
    }

    private boolean isNotSelected(Figure figure) {
        return !getValue().stream().map(Figure::getId).collect(toSet()).contains(figure.getId());
    }

    @Override
    protected void setPresentationValue(Set<Figure> s) {
    }

}
