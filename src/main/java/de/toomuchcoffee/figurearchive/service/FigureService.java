package de.toomuchcoffee.figurearchive.service;


import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FigureService {
    private final FigureRepository figureRepository;

    public List<Figure> fetch(int offset, int limit, FigureFilter filter) {
        return _fetch(filter);
    }

    public int getCount(FigureFilter filter) {
        return _fetch(filter).size();
    }

    private List<Figure> _fetch(FigureFilter filter) {
        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getFilterText()) && filter.getProductLine() != null) {
                return figureRepository.findByVerbatimStartsWithIgnoreCaseAndProductLine(filter.getFilterText(), filter.getProductLine());
            } else if (!StringUtils.isEmpty(filter.getFilterText())) {
                return figureRepository.findByVerbatimStartsWithIgnoreCase(filter.getFilterText());
            } else if (filter.getProductLine() != null) {
                return figureRepository.findByProductLine(filter.getProductLine());
            }
        }
        return figureRepository.findAll();
    }

    public List<Figure> findFigures(String query) {
        return figureRepository.findByVerbatimContainingIgnoreCase(query).subList(0, 10); // TODO
    }

    @Getter
    @Setter
    public static class FigureFilter {
        private String filterText;
        private ProductLine productLine;
    }
}