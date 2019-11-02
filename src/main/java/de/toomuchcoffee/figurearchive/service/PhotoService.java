package de.toomuchcoffee.figurearchive.service;

import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.intersection;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final PermutationService permutationService;

    public List<String> getUrls(String verbatim, int max) {
        Set<String> filter = permutationService.getPermutations(verbatim);
        return getPosts(filter).stream()
                .map(Photo::getUrl75)
                .filter(Objects::nonNull)
                .limit(max)
                .collect(toList());
    }

    public List<Photo> getPosts(Set<String> filter) {
        return photoRepository.findAll().stream()
                .filter(tp -> tp.getTags() != null)
                .filter(tp -> intersection(filter, tagSet(tp.getTags())).size() > 0)
                .sorted(comparing(tp -> difference(tagSet(tp.getTags()), filter).size()))
                .sorted(comparing(tp -> intersection(filter, tagSet(((Photo) tp).getTags())).size()).reversed())
                .collect(toList());
    }

    private Set<String> tagSet(String tags) {
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .collect(toSet());
    }

}
