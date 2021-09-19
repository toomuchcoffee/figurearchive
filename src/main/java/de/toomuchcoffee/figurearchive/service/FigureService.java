package de.toomuchcoffee.figurearchive.service;


import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import de.toomuchcoffee.figurearchive.aspect.LogExecutionTime;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.event.FigureSearchResultEvent;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import de.toomuchcoffee.figurearchive.repository.ProductLineRepository;
import lombok.*;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusProxy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static de.toomuchcoffee.figurearchive.util.PaginationHelper.paginate;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

@EventBusProxy
@VaadinSessionScope
@Service
@RequiredArgsConstructor
public class FigureService {
    private final FigureRepository figureRepository;
    private final ProductLineRepository productLineRepository;
    private final EventBus.UIEventBus eventBus;
    private final FullTextEntityManager fullTextEntityManager;
    private final Random random = new Random();

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
    public List<Figure> fuzzySearch(String searchTerm) {
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Figure.class).get();
        Query luceneQuery = qb.keyword().fuzzy()
                .withEditDistanceUpTo(2)
                .withPrefixLength(2)
                .onFields("verbatim", "productLine", "placementNo")
                .matching(searchTerm).createQuery();

        return fullTextEntityManager.createFullTextQuery(luceneQuery, Figure.class).getResultList();
    }

    @LogExecutionTime
    public List<Figure> findFigures(int page, int size, @NonNull FigureFilter filter) {
        List<Figure> figures;
        long count;
        Pageable pageable = PageRequest.of(page, size);
        if (!isBlank(filter.getFilterText())) {
            List<Figure> fetch = fuzzySearch(filter.getFilterText()).stream()
                    .filter(figure -> filter.getProductLine() == null || filter.getProductLine().equals(figure.getProductLine()))
                    .collect(toList());
            count = fetch.size();
            figures = paginate(fetch, pageable);
        } else if (filter.getProductLine() != null) {
            count = figureRepository.countByProductLine(filter.getProductLine());
            figures = figureRepository.findByProductLine(filter.getProductLine(), pageable).getContent();
        } else {
            count = figureRepository.count();
            figures = figureRepository.findAll(pageable).getContent();
        }
        eventBus.publish(this, new FigureSearchResultEvent(count, page, size, filter));
        return figures;
    }

    @LogExecutionTime
    public Map<String, Long> getProductLineInfo() {
        return productLineRepository.getProductLineCounts().stream()
                .collect(toMap(o -> (String) o[0], o -> (Long) o[1]));
    }

    @LogExecutionTime
    public List<String> getProductLines() {
        return productLineRepository.findAllByOrderByYear().stream()
                .map(ProductLine::getCode)
                .collect(toList());
    }

    @LogExecutionTime
    public Optional<Figure> getRandomFigure() {
        List<Long> ids = figureRepository.findAll().stream()
                .map(Figure::getId)
                .collect(toList());
        if (ids.isEmpty()) {
            return Optional.empty();
        }
        Long id = ids.get(random.nextInt(ids.size()));
        return figureRepository.findById(id);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FigureFilter {
        private String filterText;
        private String productLine;
    }
}