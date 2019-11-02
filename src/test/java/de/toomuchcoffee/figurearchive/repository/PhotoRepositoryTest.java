package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Photo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;

@DataJpaTest
@RunWith(SpringRunner.class)
public class PhotoRepositoryTest {
    @Autowired
    private PhotoRepository photoRepository;

    @Test
    public void findsAll() {
        Photo photo = new Photo(1L, 123L, "1280", "500", "400", "250", "100", "75", "foo,bar,baz");
        photoRepository.save(photo);
        List<Photo> photos = photoRepository.findAll();
        assertThat(photos).hasSize(1);
        assertThat(photos.get(0)).isEqualToComparingFieldByField(photo);
    }

    @Test
    public void existsByPostId() {
        Photo photo1 = new Photo();
        photo1.setPostId(123L);
        Photo photo2 = new Photo();
        photo2.setPostId(123L);
        photoRepository.saveAll(newArrayList(photo1, photo2));
        assertThat(photoRepository.existsByPostId(123L)).isTrue();
        assertThat(photoRepository.existsByPostId(456L)).isFalse();
    }
}