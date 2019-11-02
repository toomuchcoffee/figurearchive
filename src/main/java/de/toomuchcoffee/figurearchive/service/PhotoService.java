package de.toomuchcoffee.figurearchive.service;

import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.google.common.collect.Sets.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final PermutationService permutationService;

    public List<String> getUrls(String verbatim, int max) {
        Set<String> filter = permutationService.getPermutations(verbatim);
        return getPosts(filter).stream()
                .map(this::getUrl75)
                .filter(Objects::nonNull)
                .limit(max)
                .collect(toList());
    }

    private String getUrl75(Photo photo) {
        return photo.getUrls().stream()
                .filter(p -> p.getWidth() == 75)
                .findFirst()
                .map(Photo.PhotoUrl::getUrl)
                .orElse(null);
    }

    public List<Photo> getPosts(Set<String> filter) {
        return photoRepository.findAll().stream()
                .filter(tp -> tp.getTags() != null)
                .filter(tp -> intersection(filter, newHashSet(tp.getTags())).size() > 0)
                .sorted(comparing(tp -> difference(newHashSet(tp.getTags()), filter).size()))
                .sorted(comparing(tp -> intersection(filter, newHashSet(((Photo) tp).getTags())).size()).reversed())
                .collect(toList());
    }

}
