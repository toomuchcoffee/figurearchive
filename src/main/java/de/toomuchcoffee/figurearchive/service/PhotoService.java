package de.toomuchcoffee.figurearchive.service;

import com.google.common.collect.Lists;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import de.toomuchcoffee.figurearchive.aspect.LogExecutionTime;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.event.PhotoSearchResultEvent;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusProxy;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

@EventBusProxy
@VaadinSessionScope
@Service
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;

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
    public Optional<Photo> anyNotCompleted() {
        List<Long> ids = photoRepository.getIdsOfNotCompleted();
        if (ids.isEmpty()) {
            return Optional.empty();
        }
        Long id = ids.get(random.nextInt(ids.size()));
        return photoRepository.findById(id);
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

}
