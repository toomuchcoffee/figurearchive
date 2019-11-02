package de.toomuchcoffee.figurearchive.config;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import de.toomuchcoffee.figurearchive.entitiy.Figure;
import de.toomuchcoffee.figurearchive.service.FigureService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.beans.factory.annotation.Autowire.BY_NAME;

@Configuration
@RequiredArgsConstructor
public class DataProviderConfig {
    private final FigureService figureService;

    @Bean(autowire = BY_NAME)
    public ConfigurableFilterDataProvider<Figure, Void, FigureService.FigureFilter> getFigureDataProvider() {
        return DataProvider.<Figure, FigureService.FigureFilter>fromFilteringCallbacks(
                query -> figureService.fetch(query.getOffset(), query.getLimit(), query.getFilter().orElse(null)).stream(),
                query -> figureService.getCount(query.getFilter().orElse(null))).withConfigurableFilter();
    }
}
