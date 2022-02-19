package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Figure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FigureRepository extends JpaRepository<Figure, Long> {
    Page<Figure> findByProductLine(String productLine, Pageable pageable);

    List<Figure> findByProductLine(String productLine);

    long countByProductLine(String productLine);
}
