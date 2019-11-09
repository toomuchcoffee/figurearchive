package de.toomuchcoffee.figurearchive.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.vaadin.spring.events.annotation.EnableVaadinEventBus;

@Configuration
@EnableVaadinEventBus
public class EventBusConfig {
    public static class FiguresQueryEvent {
        public static final FiguresQueryEvent DUMMY = new FiguresQueryEvent();
    }

    @RequiredArgsConstructor
    @Getter
    public static class FigureModifiedEvent {
        private final String value;
        public static final FigureModifiedEvent SAVED = new FigureModifiedEvent("saved");
        public static final FigureModifiedEvent DELETED = new FigureModifiedEvent("deleted");
    }

    @RequiredArgsConstructor
    @Getter
    public static class PhotoSearchEvent {
        private final String value;
    }
}
