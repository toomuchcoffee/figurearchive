package de.toomuchcoffee.figurearchive.view.special;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.toomuchcoffee.figurearchive.service.ProductLineService;
import de.toomuchcoffee.figurearchive.service.SpecialService;
import lombok.RequiredArgsConstructor;
import org.vaadin.spring.events.EventBus;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.component.notification.Notification.Position.MIDDLE;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class SettingsPanel extends VerticalLayout {

    private final ProductLineEditor productLineEditor;
    private final ProductLineService productLineService;
    private final SpecialService specialService;
    private final EventBus.UIEventBus eventBus;

    @PostConstruct
    public void init() {
        Button btnTotalTableRows = new Button(
                "Calculate Used Table Rows",
                e -> Notification.show(
                        specialService.getTotalRowCount() + " used table rows!", 5000, MIDDLE));
        Button btnCreateProductLine = new Button("New Product Line", e -> productLineEditor.createProductLine());
        HorizontalLayout buttons = new HorizontalLayout(btnCreateProductLine, btnTotalTableRows);
        ProductLineGrid productLineGrid = new ProductLineGrid(eventBus, productLineService, productLineEditor);
        add(buttons, productLineGrid);
    }

}
