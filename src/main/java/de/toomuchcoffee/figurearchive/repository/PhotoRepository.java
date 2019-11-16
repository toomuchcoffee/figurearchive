package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PhotoRepository extends JpaRepository<Photo, Long> {
    boolean existsByPostId(Long postId);
    Page<Photo> findByFiguresEmpty(Pageable pageable);
    long countByFiguresEmpty();
    List<Photo> findByFiguresEmpty();
    Page<Photo> findByFiguresNotEmpty(Pageable pageable);
    List<Photo> findByFiguresNotEmpty();
    long countByFiguresNotEmpty();
}
