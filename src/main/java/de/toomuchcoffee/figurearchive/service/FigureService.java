package de.toomuchcoffee.figurearchive.service;


import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import de.toomuchcoffee.figurearchive.aspect.LogExecutionTime;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.event.FigureSearchResultEvent;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusProxy;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@EventBusProxy
@VaadinSessionScope
@Service
@RequiredArgsConstructor
public class FigureService {
    private final FigureRepository figureRepository;
    private final EventBus.SessionEventBus eventBus;
    private final FullTextEntityManager fullTextEntityManager;

    @LogExecutionTime
    public void save(Figure figure) {
        figureRepository.save(figure);
    }

    @LogExecutionTime
    public void delete(Figure figure) {
        figureRepository.delete(figure);
    }

    @LogExecutionTime
    @Transactional
    @SuppressWarnings("unchecked")
    public List<Figure> fuzzySearch(String searchTerm){
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Figure.class).get();
        Query luceneQuery = qb.keyword().fuzzy()
                .withEditDistanceUpTo(2)
                .withPrefixLength(2)
                .onFields("verbatim" , "productLine", "placementNo")
                .matching(searchTerm).createQuery();

        return fullTextEntityManager.createFullTextQuery(luceneQuery, Figure.class).getResultList();
    }

    @LogExecutionTime
    public List<Figure> findFigures(int page, int size, FigureFilter filter) {
        List<Figure> figures;
        long count;
        Pageable pageable = PageRequest.of(page, size);
        if (filter != null) {
            if (!StringUtils.isBlank(filter.getFilterText()) && filter.getProductLine() != null) {
                count = figureRepository.countByVerbatimContainingIgnoreCaseAndProductLine(filter.getFilterText(), filter.getProductLine());
                figures = figureRepository.findByVerbatimContainingIgnoreCaseAndProductLine(filter.getFilterText(), filter.getProductLine(), pageable).getContent();
            } else if (!StringUtils.isBlank(filter.getFilterText())) {
                count = figureRepository.countByVerbatimContainingIgnoreCase(filter.getFilterText());
                figures = figureRepository.findByVerbatimContainingIgnoreCase(filter.getFilterText(), pageable).getContent();
            } else if (filter.getProductLine() != null) {
                count = figureRepository.countByProductLine(filter.getProductLine());
                figures = figureRepository.findByProductLine(filter.getProductLine(), pageable).getContent();
            } else {
                count = figureRepository.count();
                figures = figureRepository.findAll(pageable).getContent();
            }
        } else {
            count = figureRepository.count();
            figures = figureRepository.findAll(pageable).getContent();
        }
        eventBus.publish(this, new FigureSearchResultEvent(count, page, size, filter));
        return figures;
    }

    @LogExecutionTime
    public Map<ProductLine, Long>  getProductLineInfo() {
        return figureRepository.getProductLineCounts().stream()
                .collect(toMap(o -> (ProductLine) o[0], o -> (Long) o[1]));
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