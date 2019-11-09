package de.toomuchcoffee.figurearchive.config;

import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import de.toomuchcoffee.figurearchive.service.PhotoService;
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

    @Getter
    @RequiredArgsConstructor
    public static class PhotoQueryEvent {
        private final PhotoService.PhotoFilter value;
    }

    @Getter
    @RequiredArgsConstructor
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
