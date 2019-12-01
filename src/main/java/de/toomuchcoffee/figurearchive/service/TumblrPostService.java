package de.toomuchcoffee.figurearchive.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.PhotoPost;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.entity.Photo.PhotoUrl;
import de.toomuchcoffee.figurearchive.repository.PhotoArchiveRepository;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import de.toomuchcoffee.figurearchive.util.BatchedExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toList;

@Service
@VaadinSessionScope
public class TumblrPostService {
    private static final int MAX_PAGE_SIZE = 50;

    private final boolean shouldInitialize;
    private final JumblrClient jumblrClient;
    private final PhotoRepository photoRepository;
    private final PhotoArchiveRepository photoArchiveRepository;

    private final ExecutorService executor = newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 8);

    private TumblrPostService(
            @Value("${figurearchive.tumblr.initialize:false}") boolean shouldInitialize,
            @Value("${tumblr.consumer-key}") String consumerKey,
            @Value("${tumblr.consumer-secret}") String consumerSecret,
            @Autowired PhotoRepository photoRepository,
            @Autowired PhotoArchiveRepository photoArchiveRepository) {
        this.shouldInitialize = shouldInitialize;
        this.jumblrClient = new JumblrClient(consumerKey, consumerSecret);
        this.photoRepository = photoRepository;
        this.photoArchiveRepository = photoArchiveRepository;
    }

    @PostConstruct
    private void initialize() throws IOException {
        if (shouldInitialize) {
            loadPosts();
        }
    }

    public void loadPosts() {
        Blog blog = jumblrClient.blogInfo("yaswb.tumblr.com");
        Integer postCount = blog.getPostCount();

        BiConsumer<Integer, Integer> function = (offset, pageSize) -> {
            Map<String, Integer> options = ImmutableMap.of("limit", pageSize, "offset", offset);
            executor.submit(() ->
                    jumblrClient.blogPosts("yaswb", options).stream()
                            .filter(post -> post instanceof PhotoPost)
                            .map(post -> (PhotoPost) post)
                            .filter(this::notSkipped)
                            .forEach(post -> post.getPhotos().stream()
                                    .map(photo -> mapToPhotoEntity(post, photo))
                                    .forEach(photoRepository::save)));
        };

        new BatchedExecutor(MAX_PAGE_SIZE, postCount).execute(function);
    }

    private boolean notSkipped(PhotoPost post) {
        return !isSynced(post) && !isArchived(post.getId());
    }

    private boolean isSynced(PhotoPost post) {
        return photoRepository.existsByPostId(post.getId());
    }

    private boolean isArchived(Long postId) {
        return photoArchiveRepository.getPostIds().stream()
                .map(Sets::newHashSet)
                .flatMap(Collection::stream)
                .map(Long::valueOf)
                .anyMatch(e -> e.equals(postId));
    }

    private Photo mapToPhotoEntity(PhotoPost post, com.tumblr.jumblr.types.Photo photo) {
        Photo photoEntity = new Photo();
        photoEntity.setPostId(post.getId());
        photoEntity.setTags(post.getTags().toArray(new String[0]));
        photoEntity.setUrls(photo.getSizes().stream()
                .map(s -> new PhotoUrl(s.getWidth(), s.getHeight(), s.getUrl()))
                .collect(toList()));
        return photoEntity;
    }

}
