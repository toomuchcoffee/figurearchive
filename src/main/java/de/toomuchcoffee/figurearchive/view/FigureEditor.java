package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entitiy.Figure;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.NumberFormat;
import java.util.Locale;

@SpringComponent
@UIScope
public class FigureEditor extends VerticalLayout implements KeyNotifier {

    private final FigureRepository repository;

    private Figure figure;

    private TextField verbatim = new TextField("Verbatim");
    private TextField year = new TextField("Year");

    private Button save = new Button("Save", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private Binder<Figure> binder;
    private ChangeHandler changeHandler;

    @Autowired
    public FigureEditor(FigureRepository repository) {
        this.repository = repository;

        add(verbatim, year, actions);

        binder = new Binder<>();
        binder.bind(verbatim, Figure::getVerbatim, Figure::setVerbatim);
        binder.forField(year)
                .withConverter(new StringToIntegerConverter("Enter numbers only") {
                    protected java.text.NumberFormat getFormat(Locale locale) {
                        NumberFormat format = super.getFormat(locale);
                        format.setGroupingUsed(false);
                        return format;
                    }
                })
                .withNullRepresentation(0)
                .bind(Figure::getYear, Figure::setYear);

        setSpacing(true);

        addKeyPressListener(Key.ENTER, e -> save());

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> cancel());
        setVisible(false);
    }

    private void delete() {
        repository.delete(figure);
        changeHandler.onChange();
    }

    private void save() {
        repository.save(figure);
        changeHandler.onChange();
    }

    public final void cancel() {
        changeHandler.onChange();
    }

    public final void createFigure() {
        this.figure = new Figure(null, null, null);

        binder.setBean(this.figure);

        setVisible(true);

        verbatim.focus();
    }

    public final void editFigure(Figure figure) {
        this.figure = repository.findById(figure.getId())
                .orElseThrow(() -> new IllegalStateException("Couldn't find item from list"));

        binder.setBean(this.figure);

        setVisible(true);

        verbatim.focus();
    }

    public interface ChangeHandler {
        void onChange();

    }

    public void setChangeHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

}
