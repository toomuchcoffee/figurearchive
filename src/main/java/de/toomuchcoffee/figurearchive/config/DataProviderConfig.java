package de.toomuchcoffee.figurearchive.config;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.DataChangedEvent;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.FigureQueryEvent;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

@Configuration
@RequiredArgsConstructor
public class DataProviderConfig {
    private final FigureService figureService;
    private final EventBus.ApplicationEventBus eventBus;

    @Bean
    public ConfigurableFilterDataProvider<Figure, Void, FigureFilter> getFigureDataProvider() {
        return new RefreshingDataProvider<Figure, FigureFilter>(
                query -> {
                    eventBus.publish(this, new FigureQueryEvent(query.getFilter().orElse(null)));
                    return figureService.fetch(query.getOffset(), query.getLimit(), query.getFilter().orElse(null)).stream();
                },
                query -> figureService.getCount(query.getFilter().orElse(null)),
                eventBus
        ).withConfigurableFilter();
    }

    public static class RefreshingDataProvider<T, F> extends CallbackDataProvider<T, F> {

        public RefreshingDataProvider(FetchCallback<T, F> fetchCallback, CountCallback<T, F> countCallback, EventBus eventBus) {
            super(fetchCallback, countCallback);
            eventBus.subscribe(this);
        }

        @EventBusListenerMethod
        public void update(DataChangedEvent event) {
            refreshAll();
        }
    }

}
