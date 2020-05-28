package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Figure;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static de.toomuchcoffee.figurearchive.entity.ProductLine.KENNER;
import static de.toomuchcoffee.figurearchive.entity.ProductLine.POTF2;
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
        Figure figure = new Figure(null, "Jawa", KENNER, "10012", (short) 1977, 0);
        figureRepository.save(figure);
        List<Figure> figures = figureRepository.findAll();
        assertThat(figures).hasSize(1);
        assertThat(figures.get(0)).isEqualToIgnoringGivenFields(figure, "id");
    }

    @Test
    public void saves() {
        Figure figure = new Figure(null, "Jawa", KENNER, "10012", (short) 1977, 0);
        Figure save = figureRepository.save(figure);

        Optional<Figure> byId = figureRepository.findById(save.getId());
        assertThat(byId).isPresent();
    }

    @Test
    public void getProductLineCounts() {
        figureRepository.saveAll(newArrayList(
                new Figure(null, "Jawa", KENNER, null, null, 0),
                new Figure(null, "Sand People", KENNER, null, null, 0),
                new Figure(null, "Tarkin", POTF2, null, null, 0),
                new Figure(null, "Anakin", null, null, null, 0)
        ));

        List<Object[]> productLineCounts = figureRepository.getProductLineCounts();
        assertThat(productLineCounts).hasSize(2);

        Map<Object, Object> map = productLineCounts.stream().collect(Collectors.toMap(o -> o[0], o -> o[1]));
        assertThat(map.get(KENNER)).isEqualTo(2L);
        assertThat(map.get(POTF2)).isEqualTo(1L);
    }
}