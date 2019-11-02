package de.toomuchcoffee.figurearchive.view;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.entitiy.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class FigureDataInfo extends Composite<TextField> {
    private final ConfigurableFilterDataProvider<Figure, Void, FigureFilter> figureDataProvider;
    private final EventBus.ApplicationEventBus eventBus;

    @PostConstruct
    public void init() {
        TextField textField = getContent();
        textField.setLabel("Row count");
        textField.setEnabled(false);

        eventBus.subscribe(this);
    }

    @EventBusListenerMethod
    public void update(Object event) {
        getContent().setValue(figureDataProvider.size(new Query<>()) + " figures found");
    }

}
