package de.toomuchcoffee.figurearchive.config;

import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.vaadin.spring.events.annotation.EnableVaadinEventBus;

@Configuration
@EnableVaadinEventBus
public class EventBusConfig {
    @Getter
    @RequiredArgsConstructor
    public static class FigureQueryEvent {
        private final FigureFilter value;
    }

    public static class DataChangedEvent {
        public static final DataChangedEvent DUMMY = new DataChangedEvent();
    }

    @RequiredArgsConstructor
    @Getter
    public static class PhotoSearchEvent {
        private final String value;
    }
}
