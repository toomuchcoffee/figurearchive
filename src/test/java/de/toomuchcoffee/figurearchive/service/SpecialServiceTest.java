package de.toomuchcoffee.figurearchive.service;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureEmbeddedDatabase(provider = DOCKER)
public class SpecialServiceTest {
    @Autowired
    private SpecialService specialService;

    @Test
    public void shouldGetAllRowCounts() {
        List<?> allTableRowCounts = specialService.getAllTableRowCounts();
        assertThat(allTableRowCounts).isNotEmpty();
    }

    @Test
    public void shouldGetTotalRowCount() {
        Long totalRowCount = specialService.getTotalRowCount();
        assertThat(totalRowCount).isGreaterThan(0);
    }
}