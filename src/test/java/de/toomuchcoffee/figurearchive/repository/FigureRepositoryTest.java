package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Figure;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureEmbeddedDatabase(provider = DOCKER)
public class FigureRepositoryTest {

    @Autowired
    private FigureRepository figureRepository;

    @Test
    public void findsAll() {
        Figure figure = new Figure(null, "Jawa", "KENNER", "10012", (short) 1977, 0);
        figureRepository.save(figure);
        List<Figure> figures = figureRepository.findAll();
        assertThat(figures).hasSize(1);
        assertThat(figures.get(0)).usingRecursiveComparison().ignoringFields("id").isEqualTo(figure);
    }

    @Test
    public void saves() {
        Figure figure = new Figure(null, "Jawa", "KENNER", "10012", (short) 1977, 0);
        Figure save = figureRepository.save(figure);

        Optional<Figure> byId = figureRepository.findById(save.getId());
        assertThat(byId).isPresent();
    }

}