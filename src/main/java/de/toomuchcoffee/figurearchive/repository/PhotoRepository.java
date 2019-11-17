package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PhotoRepository extends JpaRepository<Photo, Long> {
    boolean existsByPostId(Long postId);
    List<Photo> findByCompleted(boolean completed);
    long countByCompleted(boolean completed);
    List<Photo> findAllByOrderByCompletedAsc();
    Page<Photo> findAllByOrderByCompletedAsc(Pageable pageable);
}
