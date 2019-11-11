package de.toomuchcoffee.figurearchive.view.controls;

import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import de.toomuchcoffee.figurearchive.config.EventBusConfig;
import org.vaadin.spring.events.EventBus;

import java.util.stream.IntStream;

public class PaginationTabs extends Tabs {

    public PaginationTabs(int pages, String query, EventBus eventBus) {
        IntStream.range(0, pages).forEach(i -> add(new Tab(String.valueOf(i+1))));
        setSelectedIndex(0);

        addSelectedChangeListener(event -> {
            eventBus.publish(this, new EventBusConfig.PhotoSearchEvent(query, getSelectedIndex()));
        });
    }

}
