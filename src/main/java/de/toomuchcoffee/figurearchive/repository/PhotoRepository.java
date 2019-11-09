package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PhotoRepository extends JpaRepository<Photo, Long> {
    boolean existsByPostId(Long postId);
    Optional<Photo> findTop1ByFiguresIsEmpty();
}
