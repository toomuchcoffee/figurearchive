package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.ProductLine;
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
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureEmbeddedDatabase(provider = DOCKER)
public class ProductLineRepositoryTest {

    @Autowired
    private ProductLineRepository productLineRepository;

    @Autowired
    private FigureRepository figureRepository;

    @Test
    public void findsAll() {
        ProductLine productLine = new ProductLine("KENNER", "Vintage Figures", (short) 1977);
        productLineRepository.save(productLine);
        List<ProductLine> productLines = productLineRepository.findAll();
        assertThat(productLines).hasSize(1);
        assertThat(productLines.get(0)).isEqualTo(productLine);
    }

    @Test
    public void saves() {
        ProductLine productLine = new ProductLine("KENNER", "Vintage Figures", (short) 1977);
        ProductLine save = productLineRepository.save(productLine);

        Optional<ProductLine> byId = productLineRepository.findById("KENNER");
        assertThat(byId).isPresent();
    }

    @Test
    public void getProductLineCounts() {
        figureRepository.saveAll(newArrayList(
                new Figure(null, "Jawa", "KENNER", null, null, 0),
                new Figure(null, "Sand People", "KENNER", null, null, 0),
                new Figure(null, "Tarkin", "POTF2", null, null, 0),
                new Figure(null, "Anakin", null, null, null, 0)
        ));

        List<Object[]> productLineCounts = productLineRepository.getProductLineCounts();
        assertThat(productLineCounts).hasSize(2);

        Map<Object, Object> map = productLineCounts.stream().collect(Collectors.toMap(o -> o[0], o -> o[1]));
        assertThat(map.get("KENNER")).isEqualTo(2L);
        assertThat(map.get("POTF2")).isEqualTo(1L);
    }
}