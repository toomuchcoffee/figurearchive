package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FigureRepository extends JpaRepository<Figure, Long> {
    List<Figure> findByVerbatimStartsWithIgnoreCase(String verbatim);
    List<Figure> findByProductLine(ProductLine productLine);
    List<Figure> findByVerbatimStartsWithIgnoreCaseAndProductLine(String verbatim, ProductLine productLine);
}
