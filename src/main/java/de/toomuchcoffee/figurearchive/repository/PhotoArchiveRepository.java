package de.toomuchcoffee.figurearchive.repository;

import de.toomuchcoffee.figurearchive.entity.PhotoArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PhotoArchiveRepository extends JpaRepository<PhotoArchive, Long> {
    @Query("SELECT postIds " +
            "FROM PhotoArchive")
    List<String[]> getPostIds();
}
