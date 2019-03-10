package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entitiy.Figure;
import de.toomuchcoffee.figurearchive.entitiy.ProductLine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
public class FigureRepositoryTest {

    @Autowired
    private FigureRepository figureRepository;

    @Test
    public void findsAll() {
        Figure figure = new Figure(1, "Jawa", ProductLine.KENNER, 1977);
        figureRepository.save(figure);
        List<Figure> figures = figureRepository.findAll();
        assertThat(figures).hasSize(1);
        assertThat(figures.get(0)).isEqualToComparingFieldByField(figure);
    }
}