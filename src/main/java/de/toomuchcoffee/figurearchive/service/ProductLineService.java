package de.toomuchcoffee.figurearchive.service;


import de.toomuchcoffee.figurearchive.aspect.LogExecutionTime;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import de.toomuchcoffee.figurearchive.repository.ProductLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductLineService {
    private final ProductLineRepository productLineRepository;
    private final FigureRepository figureRepository;

    @LogExecutionTime
    public void save(ProductLine productLine) {
        productLineRepository.save(productLine);
    }

    @LogExecutionTime
    public void delete(ProductLine productLine) {
        productLineRepository.delete(productLine);
    }

    @LogExecutionTime
    public List<ProductLine> findProductLines() {
        return productLineRepository.findAllByOrderByYear();
    }

    @LogExecutionTime
    public boolean isUsed(String productLine) {
        return figureRepository.countByProductLine(productLine) > 0;
    }
}