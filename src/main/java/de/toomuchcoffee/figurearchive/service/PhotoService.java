package de.toomuchcoffee.figurearchive.service;

import de.toomuchcoffee.figurearchive.config.EventBusConfig;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.vaadin.spring.events.EventBus;

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

    public List<Photo> findPhotos(String query) {
        if (StringUtils.isBlank(query)) {
            return photoRepository.findAll();
        } else {
            Set<String> filter = permutationService.getPermutations(query);
            return photoRepository.findAll().stream()
                    .filter(photo -> photo.getTags() != null)
                    .filter(photo -> intersection(filter, newHashSet(photo.getTags())).size() > 0)
                    .sorted(comparing(photo -> difference(newHashSet(photo.getTags()), filter).size()))
                    .sorted(comparing(photo -> intersection(filter, newHashSet(((Photo) photo).getTags())).size()).reversed())
                    .collect(toList());
        }
    }

    public List<Photo> findPhotos(int page, int size, String query) {
        List<Photo> photos;
        if (StringUtils.isBlank(query)) {
            photos = photoRepository.findAll(PageRequest.of(page, size)).getContent();
        } else {
            Set<String> filter = permutationService.getPermutations(query);
            photos = photoRepository.findAll().stream()
                    .filter(photo -> photo.getTags() != null)
                    .filter(photo -> intersection(filter, newHashSet(photo.getTags())).size() > 0)
                    .sorted(comparing(photo -> difference(newHashSet(photo.getTags()), filter).size()))
                    .sorted(comparing(photo -> intersection(filter, newHashSet(((Photo) photo).getTags())).size()).reversed())
                    .collect(toList());
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, photos.size());
            photos = photos.subList(startIndex, endIndex);
        }
        eventBus.publish(this, new EventBusConfig.PhotoSearchResultEvent(photos.size()));
        return photos;
    }

}
