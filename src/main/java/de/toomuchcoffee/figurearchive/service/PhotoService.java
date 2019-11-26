package de.toomuchcoffee.figurearchive.service;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import de.toomuchcoffee.figurearchive.aspect.LogExecutionTime;
import de.toomuchcoffee.figurearchive.entity.Figure;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.event.PhotoSearchResultEvent;
import de.toomuchcoffee.figurearchive.repository.FigureRepository;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusProxy;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.collect.Sets.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

@EventBusProxy
@VaadinSessionScope
@Service
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final FigureRepository figureRepository;

    private final EventBus.SessionEventBus eventBus;
    private final PermutationService permutationService;

    @LogExecutionTime
    public Optional<Photo> findById(Long id) {
        return photoRepository.findById(id);
    }

    @LogExecutionTime
    public List<Photo> suggestPhotos(String query) {
        if (isBlank(query)) {
            return photoRepository.findByCompleted(false);
        } else {
            Set<String> filter = permutationService.getPermutations(query);
            return photoRepository.findByCompleted(false).stream()
                    .filter(photo -> intersection(filter, newHashSet(photo.getTags())).size() > 0)
                    .sorted(comparing(photo -> difference(newHashSet(photo.getTags()), filter).size()))
                    .sorted(comparing(photo -> intersection(filter, newHashSet(((Photo) photo).getTags())).size()).reversed())
                    .collect(toList());
        }
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
    public OwningSideOfRelation<Figure, Photo> prepareOwningSideOfRelation(Photo photo) {
        return new OwningSideOfRelation<>(Figure::getPhotos, Photo::getFigures, photo);
    }

    @LogExecutionTime
    public void save(Photo photo, OwningSideOfRelation<Figure, Photo> owningSideOfRelation) {
        photoRepository.save(photo);
        figureRepository.saveAll(owningSideOfRelation.collectChangedOwners());
    }

    public static class OwningSideOfRelation<O, E> {
        private final Function<O, Set<E>> relation;
        private final Function<E, Set<O>> backRelation;
        private final E entity;
        private final Set<O> beforeChange;

        private OwningSideOfRelation(Function<O, Set<E>> relation, Function<E, Set<O>> backRelation, E entity) {
            this.relation = relation;
            this.backRelation = backRelation;
            this.entity = entity;
            this.beforeChange = backRelation.apply(entity);
        }

        Set<O> collectChangedOwners() {
            Set<O> afterChange = backRelation.apply(entity);
            Set<O> deletedFrom = difference(beforeChange, afterChange);
            deletedFrom.forEach(e -> relation.apply(e).remove(entity));
            Set<O> addedTo = difference(afterChange, beforeChange);
            addedTo.forEach(e -> relation.apply(e).add(entity));
            return union(addedTo, deletedFrom);
        }
    }
}
