package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PhotoRepository extends JpaRepository<Photo, Long> {
    boolean existsByPostId(Long postId);
    List<Photo> findByCompleted(boolean completed);
    long countByCompleted(boolean completed);
    List<Photo> findAllByOrderByCompletedAsc();
    Page<Photo> findAllByOrderByCompletedAsc(Pageable pageable);

    @Query("SELECT id " +
            "FROM Photo " +
            "WHERE completed = 'false'")
    List<Long> getIdsOfNotCompleted();

    @Query("SELECT tags " +
            "FROM Photo")
    List<String[]> getTags();
}
