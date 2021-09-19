package de.toomuchcoffee.figurearchive.view.special;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.service.ProductLineService;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class SettingsPanel extends VerticalLayout {

    private final ProductLineEditor productLineEditor;
    private final ProductLineService productLineService;
    private final EventBus.UIEventBus eventBus;

    @PostConstruct
    public void init() {
        Button btnCreateProductLine = new Button("New Product Line", e -> productLineEditor.createProductLine());
        ProductLineGrid productLineGrid = new ProductLineGrid(eventBus, productLineService, productLineEditor);
        add(btnCreateProductLine, productLineGrid);
    }

}
