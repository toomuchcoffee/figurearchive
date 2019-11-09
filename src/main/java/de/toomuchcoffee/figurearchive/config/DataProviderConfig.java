package de.toomuchcoffee.figurearchive.config;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.FiguresQueryEvent;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaadin.spring.events.EventBus;

@Configuration
@RequiredArgsConstructor
public class DataProviderConfig {
    private final FigureService figureService;
    private final EventBus.ApplicationEventBus eventBus;

    @Bean
    public ConfigurableFilterDataProvider<Figure, Void, FigureService.FigureFilter> getFigureDataProvider() {
        return DataProvider.<Figure, FigureService.FigureFilter>fromFilteringCallbacks(
                query -> {
                    eventBus.publish(this, FiguresQueryEvent.DUMMY);
                    return figureService.fetch(query.getOffset(), query.getLimit(), query.getFilter().orElse(null)).stream();
                },
                query -> figureService.getCount(query.getFilter().orElse(null))).withConfigurableFilter();
    }

}
