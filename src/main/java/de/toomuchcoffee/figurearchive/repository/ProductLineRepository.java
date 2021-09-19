package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.ProductLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductLineRepository extends JpaRepository<ProductLine, String> {
    List<ProductLine> findAllByOrderByYear();

    @Query("SELECT f.productLine, count(f) " +
            "FROM Figure f " +
            "WHERE f.productLine != null " +
            "GROUP BY f.productLine " +
            "HAVING count(f) > 0")
    List<Object[]> getProductLineCounts();
}
