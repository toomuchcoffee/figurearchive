package de.toomuchcoffee.figurearchive.service;

import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.repository.PhotoArchiveRepository;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.vaadin.spring.events.EventBus;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureEmbeddedDatabase(provider = DOCKER)
public class PhotoServiceTest {
    @Autowired
    private PhotoArchiveRepository photoArchiveRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @MockBean
    private EventBus.UIEventBus eventBus;

    private PhotoService photoService;

    @Before
    public void setUp() throws Exception {
        photoService = new PhotoService(photoRepository, photoArchiveRepository, eventBus);
    }

    @Test
    public void archive() {
        Photo photo1 = new Photo(null, 123L, null, null, null, false);
        Photo photo2 = new Photo(null, 456L, null, null, null, false);
        photoRepository.saveAll(newArrayList(photo1, photo2));

        assertThat(photoRepository.findAll()).hasSize(2);
        assertThat(photoArchiveRepository.findAll()).hasSize(0);

        photoService.archive(photo1);

        assertThat(photoRepository.findAll()).hasSize(1);
        assertThat(photoArchiveRepository.findAll()).hasSize(1);
        assertThat(photoService.getArchivedPostIds()).isEqualTo(newHashSet(123L));

        photoService.archive(photo2);

        assertThat(photoRepository.findAll()).hasSize(0);
        assertThat(photoArchiveRepository.findAll()).hasSize(1);
        assertThat(photoService.getArchivedPostIds()).isEqualTo(newHashSet(123L, 456L));
    }
}