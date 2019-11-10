package de.toomuchcoffee.figurearchive.config;

import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.vaadin.spring.events.annotation.EnableVaadinEventBus;

@Configuration
@EnableVaadinEventBus
public class EventBusConfig {
    public static class DataChangedEvent {
        public static final DataChangedEvent DUMMY = new DataChangedEvent();
    }

    @RequiredArgsConstructor
    @Getter
    public static class PhotoSearchByVerbatimEvent {
        private final String value;
    }

    @RequiredArgsConstructor
    @Getter
    public static class PhotoSearchEvent {
        private final String value;
    }

    @RequiredArgsConstructor
    @Getter
    public static class PhotoSearchResultEvent {
        private final int count;
    }

    @RequiredArgsConstructor
    @Getter
    public static class FigureSearchEvent {
        private final FigureFilter value;
    }

    @RequiredArgsConstructor
    @Getter
    public static class FigureSearchResultEvent {
        private final int count;
    }
}
