package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Photo;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.google.common.collect.Sets.newHashSet;
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
        Photo photo = new Photo(null, 123L, newArrayList(), new String[0], newHashSet(), false);
        photoRepository.save(photo);
        List<Photo> photos = photoRepository.findAll();
        assertThat(photos).hasSize(1);
        assertThat(photos.get(0)).usingRecursiveComparison().ignoringFields("").isEqualTo(photo);
    }

    @Test
    public void existsByPostId() {
        Photo photo1 = new Photo(1L, 123L, newArrayList(), new String[0], newHashSet(), false);
        Photo photo2 = new Photo(2L, 123L, newArrayList(), new String[0], newHashSet(), false);
        photoRepository.saveAll(newArrayList(photo1, photo2));
        assertThat(photoRepository.existsByPostId(123L)).isTrue();
        assertThat(photoRepository.existsByPostId(456L)).isFalse();
    }

    @Test
    public void findsByCompleted() {
        Photo photo1 = new Photo(1L, 123L, newArrayList(), new String[0], newHashSet(), false);
        Photo photo2 = new Photo(2L, 456L, newArrayList(), new String[0], newHashSet(), true);
        photoRepository.saveAll(newArrayList(photo1, photo2));
        assertThat(photoRepository.countByCompleted(true)).isEqualTo(1);
        assertThat(photoRepository.findByCompleted(true)).hasSize(1);
        assertThat(photoRepository.findByCompleted(true).get(0).getPostId()).isEqualTo(456L);
        assertThat(photoRepository.countByCompleted(false)).isEqualTo(1);
        assertThat(photoRepository.findByCompleted(false)).hasSize(1);
        assertThat(photoRepository.findByCompleted(false).get(0).getPostId()).isEqualTo(123L);
    }

    @Test
    public void sortsByCompleted() {
        Photo photo1 = new Photo(1L, 123L, newArrayList(), new String[0], newHashSet(), false);
        Photo photo2 = new Photo(2L, 123L, newArrayList(), new String[0], newHashSet(), true);
        Photo photo3 = new Photo(3L, 123L, newArrayList(), new String[0], newHashSet(), false);
        photoRepository.saveAll(newArrayList(photo1, photo2, photo3));
        assertThat(photoRepository.findAllByOrderByCompletedAsc()).hasSize(3);
        assertThat(photoRepository.findAllByOrderByCompletedAsc().get(2).isCompleted()).isTrue();
        assertThat(photoRepository.findAllByOrderByCompletedAsc(PageRequest.of(1, 2)).getContent()).hasSize(1);
        assertThat(photoRepository.findAllByOrderByCompletedAsc(PageRequest.of(1, 2)).getContent().get(0).isCompleted()).isTrue();
    }

    @Test
    public void getIdsOfNotCompleted() {
        Photo photo1 = new Photo(1L, 123L, newArrayList(), new String[0], newHashSet(), false);
        Photo photo2 = new Photo(2L, 123L, newArrayList(), new String[0], newHashSet(), true);
        Photo photo3 = new Photo(3L, 123L, newArrayList(), new String[0], newHashSet(), false);
        photoRepository.saveAll(newArrayList(photo1, photo2, photo3));
        List<Long> result = photoRepository.getIdsOfNotCompleted();
        assertThat(result).hasSize(2);
    }

    @Test
    public void getTags() {
        Photo photo1 = new Photo(1L, 123L, newArrayList(), new String[]{"foo", "bar"}, newHashSet(), false);
        Photo photo2 = new Photo(2L, 123L, newArrayList(), new String[] {"baz"}, newHashSet(), true);
        photoRepository.saveAll(newArrayList(photo1, photo2));
        List<String[]> result = photoRepository.getTags();
        assertThat(result).hasSize(2);
    }
}