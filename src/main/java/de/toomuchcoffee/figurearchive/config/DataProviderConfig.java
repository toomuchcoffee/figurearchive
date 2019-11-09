package de.toomuchcoffee.figurearchive.config;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.FigureQueryEvent;
import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoQueryEvent;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.service.FigureService;
import de.toomuchcoffee.figurearchive.service.FigureService.FigureFilter;
import de.toomuchcoffee.figurearchive.service.PhotoService;
import de.toomuchcoffee.figurearchive.service.PhotoService.PhotoFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaadin.spring.events.EventBus;

@Configuration
@RequiredArgsConstructor
public class DataProviderConfig {
    private final FigureService figureService;
    private final PhotoService photoService;
    private final EventBus.ApplicationEventBus eventBus;

    @Bean
    public ConfigurableFilterDataProvider<Figure, Void, FigureFilter> getFigureDataProvider() {
        return DataProvider.<Figure, FigureFilter>fromFilteringCallbacks(
                query -> {
                    eventBus.publish(this, new FigureQueryEvent(query.getFilter().orElse(null)));
                    return figureService.fetch(query.getOffset(), query.getLimit(), query.getFilter().orElse(null)).stream();
                },
                query -> figureService.getCount(query.getFilter().orElse(null))).withConfigurableFilter();
    }

    @Bean
    public ConfigurableFilterDataProvider<Photo, Void, PhotoFilter> getPhotoDataProvider() {
        return DataProvider.<Photo, PhotoFilter>fromFilteringCallbacks(
                query -> {
                    eventBus.publish(this, new PhotoQueryEvent(query.getFilter().orElse(null)));
                    return photoService.fetch(query.getOffset(), query.getLimit(), query.getFilter().orElse(null)).stream();
                },
                query -> photoService.getCount(query.getFilter().orElse(null))).withConfigurableFilter();
    }

}
