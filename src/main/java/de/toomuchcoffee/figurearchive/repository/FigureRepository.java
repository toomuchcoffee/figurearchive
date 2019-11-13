package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FigureRepository extends JpaRepository<Figure, Long> {
    List<Figure> findByVerbatimContainingIgnoreCase(String verbatim);
    Page<Figure> findByVerbatimStartsWithIgnoreCase(String verbatim, Pageable pageable);
    long countByVerbatimStartsWithIgnoreCase(String verbatim);
    Page<Figure> findByProductLine(ProductLine productLine, Pageable pageable);
    long countByProductLine(ProductLine productLine);
    Page<Figure> findByVerbatimStartsWithIgnoreCaseAndProductLine(String verbatim, ProductLine productLine, Pageable pageable);
    long countByVerbatimStartsWithIgnoreCaseAndProductLine(String verbatim, ProductLine productLine);
}
