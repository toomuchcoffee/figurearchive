package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Photo;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureEmbeddedDatabase(provider = DOCKER)
public class PhotoRepositoryTest {
    @Autowired
    private PhotoRepository photoRepository;

    @Test
    public void findsAll() {
        Photo photo = new Photo(null, 123L, newArrayList(), new String[0]);
        photoRepository.save(photo);
        List<Photo> photos = photoRepository.findAll();
        assertThat(photos).hasSize(1);
        assertThat(photos.get(0)).isEqualToIgnoringGivenFields(photo, "id");
    }

    @Test
    public void existsByPostId() {
        Photo photo1 = new Photo(1L, 123L, newArrayList(), new String[0]);
        Photo photo2 = new Photo(2L, 123L, newArrayList(), new String[0]);
        photoRepository.saveAll(newArrayList(photo1, photo2));
        assertThat(photoRepository.existsByPostId(123L)).isTrue();
        assertThat(photoRepository.existsByPostId(456L)).isFalse();
    }
}