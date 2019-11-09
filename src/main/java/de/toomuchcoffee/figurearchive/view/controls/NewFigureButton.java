package de.toomuchcoffee.figurearchive.view.controls;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.view.FigureEditor;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.component.icon.VaadinIcon.PLUS;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class NewFigureButton extends Button {

    private final FigureEditor figureEditor;

    @PostConstruct
    public void init() {
        setText("New Figure");
        setIcon(PLUS.create());
        addClickListener(e -> figureEditor.createFigure());
    }
}
