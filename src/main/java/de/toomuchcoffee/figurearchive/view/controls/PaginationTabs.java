package de.toomuchcoffee.figurearchive.view.controls;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoSearchEvent;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoSearchResultEvent;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import java.util.stream.IntStream;

public class PaginationTabs extends Div {
    private final EventBus.SessionEventBus eventBus;

    public PaginationTabs(EventBus.SessionEventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.subscribe(this);
    }

    @EventBusListenerMethod
    public void update(PhotoSearchResultEvent event) {
        if (event.getPage() == 0) {
            removeAll();
            int pages = (int) Math.ceil((double) event.getCount() / (double) event.getSize());
            Tabs tabs = new Tabs();
            add(tabs);
            IntStream.range(0, pages).forEach(i -> tabs.add(new Tab(String.valueOf(i + 1))));
            tabs.setSelectedIndex(0);

            tabs.addSelectedChangeListener(e -> {
                eventBus.publish(this, new PhotoSearchEvent(event.getQuery(), tabs.getSelectedIndex()));
            });
        }
    }

}
