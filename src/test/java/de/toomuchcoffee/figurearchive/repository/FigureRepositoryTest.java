package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.Photo;
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
import static com.google.common.collect.Sets.newHashSet;
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

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    public void findsAll() {
        Figure figure = new Figure(null, "Jawa", KENNER, "10012", (short) 1977, newHashSet());
        figureRepository.save(figure);
        List<Figure> figures = figureRepository.findAll();
        assertThat(figures).hasSize(1);
        assertThat(figures.get(0)).isEqualToIgnoringGivenFields(figure, "id");
    }

    @Test
    public void savesWithPhotos() {
        photoRepository.save(new Photo());
        List<Photo> photos = photoRepository.findAll();

        Figure figure = new Figure(null, "Jawa", KENNER, "10012", (short) 1977, newHashSet(photos));
        Figure save = figureRepository.save(figure);

        Optional<Figure> byId = figureRepository.findById(save.getId());
        assertThat(byId).isPresent();
        assertThat(byId.get().getPhotos()).hasSize(1);
    }

    @Test
    public void getProductLineCounts() {
        figureRepository.saveAll(newArrayList(
                new Figure(null, "Jawa", KENNER, null, null, newHashSet()),
                new Figure(null, "Sand People", KENNER, null, null, newHashSet()),
                new Figure(null, "Tarkin", POTF2, null, null, newHashSet()),
                new Figure(null, "Anakin", null, null, null, newHashSet())
        ));

        List<Object[]> productLineCounts = figureRepository.getProductLineCounts();
        assertThat(productLineCounts).hasSize(2);

        Map<Object, Object> map = productLineCounts.stream().collect(Collectors.toMap(o -> o[0], o -> o[1]));
        assertThat(map.get(KENNER)).isEqualTo(2L);
        assertThat(map.get(POTF2)).isEqualTo(1L);
    }
}