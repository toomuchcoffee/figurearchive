package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FigureRepository extends JpaRepository<Figure, Long> {
    Page<Figure> findByProductLine(ProductLine productLine, Pageable pageable);
    long countByProductLine(ProductLine productLine);

    @Query("SELECT f.productLine, count(f) " +
            "FROM Figure f " +
            "WHERE f.productLine != null " +
            "GROUP BY f.productLine " +
            "HAVING count(f) > 0")
    List<Object[]> getProductLineCounts();
}
