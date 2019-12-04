package de.toomuchcoffee.figurearchive.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import de.toomuchcoffee.figurearchive.aspect.LogExecutionTime;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.entity.PhotoArchive;
import de.toomuchcoffee.figurearchive.event.PhotoSearchResultEvent;
import de.toomuchcoffee.figurearchive.repository.PhotoArchiveRepository;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusProxy;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.tuple.ImmutablePair.of;

@EventBusProxy
@VaadinSessionScope
@Service
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final PhotoArchiveRepository photoArchiveRepository;

    private final EventBus.SessionEventBus eventBus;

    private final Random random = new Random();

    @LogExecutionTime
    public Optional<Photo> findById(Long id) {
        return photoRepository.findById(id);
    }

    @LogExecutionTime
    public List<String> getAllTags() {
        return photoRepository.getTags().stream()
                .map(Lists::newArrayList)
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .collect(toList());
    }

    @LogExecutionTime
    public Pair<Optional<Photo>, Integer> anyNotCompleted() {
        List<Long> ids = photoRepository.getIdsOfNotCompleted();
        if (ids.isEmpty()) {
            return of(Optional.empty(), 0);
        }
        Long id = ids.get(random.nextInt(ids.size()));
        return of(photoRepository.findById(id), ids.size());
    }

    @LogExecutionTime
    public List<Photo> findPhotosByTag(int page, int size, String tag) {
        List<Photo> photos;
        long count;
        if (isBlank(tag)) {
            Pageable pageable = PageRequest.of(page, size);
            count = photoRepository.count();
            photos = photoRepository.findAllByOrderByCompletedAsc(pageable).getContent();
        } else {
            photos = photoRepository.findAllByOrderByCompletedAsc().stream()
                    .filter(photo -> Arrays.stream(photo.getTags()).anyMatch(t -> t.equalsIgnoreCase(tag)))
                    .collect(toList());
            count = photos.size();
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, photos.size());
            photos = photos.subList(startIndex, endIndex);
        }
        eventBus.publish(this, new PhotoSearchResultEvent(count, page, size, tag));
        return photos;
    }

    @LogExecutionTime
    public void save(Photo photo) {
        photoRepository.save(photo);
    }

    private PhotoArchive getPhotoArchive() {
        if (photoArchiveRepository.findAll().isEmpty()) {
            photoArchiveRepository.save(new PhotoArchive());
        }
        return photoArchiveRepository.findAll().get(0);
    }

    @VisibleForTesting
    Set<Long> getArchivedPostIds() {
        return photoArchiveRepository.getPostIds().stream()
                .map(Sets::newHashSet)
                .flatMap(Collection::stream)
                .map(Long::valueOf)
                .collect(toSet());
    }

    @LogExecutionTime
    @Transactional
    public void archive(Photo photo) {
        Set<Long> postIds = getArchivedPostIds();
        postIds.add(photo.getPostId());
        PhotoArchive photoArchive = getPhotoArchive();
        photoArchive.setPostIds(postIds.toArray(new Long[0]));
        photoArchiveRepository.save(photoArchive);
        photoRepository.delete(photo);
    }

}
