package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entitiy.Figure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FigureRepository extends JpaRepository<Figure, Integer> {
    List<Figure> findByVerbatimStartsWithIgnoreCase(String q);
}
