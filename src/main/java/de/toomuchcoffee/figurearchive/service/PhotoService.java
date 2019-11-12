package de.toomuchcoffee.figurearchive.service;

import de.toomuchcoffee.figurearchive.config.EventBusConfig.PhotoSearchResultEvent;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.vaadin.spring.events.EventBus;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final EventBus.ApplicationEventBus eventBus;
    private final PermutationService permutationService;

    public List<Photo> suggestPhotos(String query) {
        if (StringUtils.isBlank(query)) {
            return photoRepository.findAll();
        } else {
            Set<String> filter = permutationService.getPermutations(query);
            return photoRepository.findAll().stream()
                    .filter(photo -> intersection(filter, newHashSet(photo.getTags())).size() > 0)
                    .sorted(comparing(photo -> difference(newHashSet(photo.getTags()), filter).size()))
                    .sorted(comparing(photo -> intersection(filter, newHashSet(((Photo) photo).getTags())).size()).reversed())
                    .collect(toList());
        }
    }

    public List<Photo> findPhotosByTag(int page, int size, String tag) {
        List<Photo> photos;
        long count;
        if (StringUtils.isBlank(tag)) {
            count = photoRepository.count();
            photos = photoRepository.findAll(PageRequest.of(page, size)).getContent();
        } else {
            photos = photoRepository.findAll().stream()
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

}
