package de.toomuchcoffee.figurearchive.config;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaadin.spring.events.EventBus;

@Configuration
@RequiredArgsConstructor
public class DataProviderConfig {
    private static final Object DUMMY_EVENT = new Object();

    private final FigureService figureService;
    private final EventBus.ApplicationEventBus eventBus;

    @Bean
    public ConfigurableFilterDataProvider<Figure, Void, FigureService.FigureFilter> getFigureDataProvider() {
        return DataProvider.<Figure, FigureService.FigureFilter>fromFilteringCallbacks(
                query -> {
                    eventBus.publish(this, DUMMY_EVENT);
                    return figureService.fetch(query.getOffset(), query.getLimit(), query.getFilter().orElse(null)).stream();
                },
                query -> figureService.getCount(query.getFilter().orElse(null))).withConfigurableFilter();
    }

}
