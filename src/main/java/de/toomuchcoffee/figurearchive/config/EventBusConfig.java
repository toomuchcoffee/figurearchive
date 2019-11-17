package de.toomuchcoffee.figurearchive.config;

import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import de.toomuchcoffee.figurearchive.service.PhotoService.PhotoFilter;
import de.toomuchcoffee.figurearchive.view.controls.PaginationTabs.SearchEvent;
import de.toomuchcoffee.figurearchive.view.controls.PaginationTabs.SearchResultEvent;
import lombok.*;
import org.springframework.context.annotation.Configuration;
import org.vaadin.spring.events.annotation.EnableVaadinEventBus;

@Configuration
@EnableVaadinEventBus
public class EventBusConfig {
    @RequiredArgsConstructor
    @Getter
    public static class DataChangedEvent<T> {
        private final T value;
        private final Operation operation;

        public enum Operation {
            CREATED, UPDATED, DELETED
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class PhotoSearchByVerbatimEvent {
        private final String value;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhotoSearchEvent implements SearchEvent<PhotoFilter> {
        private PhotoFilter filter;
        private int page;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FigureSearchEvent implements SearchEvent<FigureFilter> {
        private FigureFilter filter;
        private int page;
    }

    @Getter
    @RequiredArgsConstructor
    public static class PhotoSearchResultEvent implements SearchResultEvent<String> {
        private final long count;
        private final int page;
        private final int size;
        private final String filter;
    }

    @Getter
    @RequiredArgsConstructor
    public static class FigureSearchResultEvent implements SearchResultEvent<FigureFilter> {
        private final long count;
        private final int page;
        private final int size;
        private final FigureFilter filter;
    }
}
