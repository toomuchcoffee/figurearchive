package de.toomuchcoffee.figurearchive.service;


import de.toomuchcoffee.figurearchive.config.EventBusConfig.FigureSearchResultEvent;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.vaadin.spring.events.EventBus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FigureService {
    private final FigureRepository figureRepository;
    private final EventBus.ApplicationEventBus eventBus;

    public List<Figure> findFigures(FigureFilter filter) {
        List<Figure> figures;
        if (filter != null) {
            if (!StringUtils.isBlank(filter.getFilterText()) && filter.getProductLine() != null) {
                figures = figureRepository.findByVerbatimStartsWithIgnoreCaseAndProductLine(filter.getFilterText(), filter.getProductLine());
            } else if (!StringUtils.isBlank(filter.getFilterText())) {
                figures = figureRepository.findByVerbatimStartsWithIgnoreCase(filter.getFilterText());
            } else if (filter.getProductLine() != null) {
                figures = figureRepository.findByProductLine(filter.getProductLine());
            } else {
                figures = figureRepository.findAll();
            }
        } else {
            figures = figureRepository.findAll();
        }
        eventBus.publish(this, new FigureSearchResultEvent(figures.size()));
        return figures;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FigureFilter {
        private String filterText;
        private ProductLine productLine;
    }
}