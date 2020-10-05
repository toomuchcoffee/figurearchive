package de.toomuchcoffee.figurearchive.view.controls;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import lombok.SneakyThrows;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import java.util.stream.IntStream;

public class PaginationTabs<R extends PaginationTabs.SearchResultEvent<F>, S extends PaginationTabs.SearchEvent<F>, F> extends Div {
    private final UIEventBus eventBus;
    private final Class<R> searchResultEventType;
    private final Class<S> searchEventType;

    private Tabs tabs;

    public PaginationTabs(UIEventBus eventBus, Class<R> searchResultEventType, Class<S> searchEventType) {
        this.eventBus = eventBus;
        this.searchEventType = searchEventType;
        this.searchResultEventType = searchResultEventType;
        eventBus.subscribe(this);
    }

    @EventBusListenerMethod
    public void update(SearchResultEvent<F> event) {
        if (event.getPage() == 0 && searchResultEventType.isInstance(event)) {
            removeAll();
            int pages = (int) Math.ceil((double) event.getCount() / (double) event.getSize());
            if (pages > 0) {
                tabs = new Tabs();
                tabs.setMaxWidth("282px");
                add(tabs);
                IntStream.range(0, pages).forEach(i -> tabs.add(new Tab(String.valueOf(i + 1))));
                tabs.setSelectedIndex(0);

                tabs.addSelectedChangeListener(e -> {
                    eventBus.publish(this, newSearchEvent(event.getFilter(), tabs.getSelectedIndex()));
                });
            }
        }
    }

    @SneakyThrows
    private S newSearchEvent(F filter, int page) {
        S instance = searchEventType.newInstance();
        instance.setFilter(filter);
        instance.setPage(page);
        return instance;
    }

    public interface SearchResultEvent<F> {
        long getCount();
        int getPage();
        int getSize();
        F getFilter();
    }

    public interface SearchEvent<F> {
        F getFilter();
        int getPage();
        void setFilter(F filter);
        void setPage(int page);
    }
}
