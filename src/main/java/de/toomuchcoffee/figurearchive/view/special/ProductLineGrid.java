package de.toomuchcoffee.figurearchive.view.special;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.event.ProductLineChangedEvent;
import de.toomuchcoffee.figurearchive.service.ProductLineService;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProductLineGrid extends Grid<ProductLine> {
    private final ProductLineService productLineService;

    public ProductLineGrid(
            EventBus.UIEventBus eventBus,
            ProductLineService productLineService,
            ProductLineEditor productLineEditor) {
        super(ProductLine.class);
        this.productLineService = productLineService;
        addItemClickListener(e -> productLineEditor.editProductLine(e.getItem()));

        setColumns("code", "description", "year");
        getColumns().forEach(column -> column.setAutoWidth(true));

        addAttachListener(e -> eventBus.subscribe(this));
        addDetachListener(e -> eventBus.unsubscribe(this));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        setItems(productLineService.findProductLines());
    }

    @EventBusListenerMethod
    public void update(ProductLineChangedEvent event) {
        List<ProductLine> items = new ArrayList<>(getItems());
        switch (event.getOperation()) {
            case UPDATED: {
                if (!items.contains(event.getValue())) {
                    items.add(event.getValue());
                    setItems(items);
                    getDataProvider().refreshAll();
                } else {
                    getDataProvider().refreshItem(event.getValue());
                }
                break;
            }
            case DELETED: {
                items.remove(event.getValue());
                setItems(items);
                getDataProvider().refreshAll();
                break;
            }
            default:
                getDataProvider().refreshAll();
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<ProductLine> getItems() {
        return ((ListDataProvider<ProductLine>) getDataProvider()).getItems();
    }

}
