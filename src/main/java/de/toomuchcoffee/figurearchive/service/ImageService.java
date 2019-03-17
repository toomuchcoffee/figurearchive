package de.toomuchcoffee.figurearchive.service;

import de.toomuchcoffee.figurearchive.service.TumblrService.TumblrPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final TumblrService tumblrService;
    private final PermutationService permutationService;

    public List<String> getThumbnailUrls(String verbatim, int max) {
        Set<String> filter = permutationService.getPermutations(verbatim);
        return tumblrService.getPosts(filter).stream()
                .filter(p -> p.getPhotoUrl75() != null)
                .map(TumblrPost::getPhotoUrl75)
                .limit(max)
                .collect(Collectors.toList());
    }

}
