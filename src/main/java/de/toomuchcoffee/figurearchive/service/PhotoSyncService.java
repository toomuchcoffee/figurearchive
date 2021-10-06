package de.toomuchcoffee.figurearchive.service;

import com.google.common.collect.Sets;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import de.toomuchcoffee.figurearchive.entity.Photo;
import de.toomuchcoffee.figurearchive.entity.Photo.PhotoUrl;
import de.toomuchcoffee.figurearchive.repository.PhotoArchiveRepository;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import de.toomuchcoffee.figurearchive.tumblr.BlocksPost;
import de.toomuchcoffee.figurearchive.tumblr.BlogInfo;
import de.toomuchcoffee.figurearchive.tumblr.Content;
import de.toomuchcoffee.figurearchive.util.BatchedExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toList;

@Service
@VaadinSessionScope
public class PhotoSyncService {
    private static final int MAX_PAGE_SIZE = 50;
    public static final String POST_TYPE_BLOCKS = "blocks";

    private final boolean shouldInitialize;
    private final TumblrService tumblrService;
    private final PhotoRepository photoRepository;
    private final PhotoArchiveRepository photoArchiveRepository;

    private final ExecutorService executor = newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 8);

    private PhotoSyncService(
            @Value("${figurearchive.tumblr.initialize:false}") boolean shouldInitialize,
            @Autowired TumblrService tumblrService,
            @Autowired PhotoRepository photoRepository,
            @Autowired PhotoArchiveRepository photoArchiveRepository) {
        this.shouldInitialize = shouldInitialize;
        this.tumblrService = tumblrService;
        this.photoRepository = photoRepository;
        this.photoArchiveRepository = photoArchiveRepository;
    }

    @PostConstruct
    public void initialize() {
        if (shouldInitialize) {
            loadPosts();
        }
    }

    public void loadPosts() {
        BlogInfo blogInfo = tumblrService.getInfo();
        int postCount = blogInfo.getTotalPosts();

        BiConsumer<Integer, Integer> function = (offset, pageSize) -> executor.submit(() ->
                tumblrService.getPosts(offset, pageSize).stream()
                        .filter(p -> POST_TYPE_BLOCKS.equals(p.getType()))
                        .filter(this::notSkipped)
                        .forEach(post -> post.getContent().stream()
                                .filter(content -> !CollectionUtils.isEmpty(content.getMedia()))
                                .forEach(content -> createPhoto(post, content))));

        new BatchedExecutor(MAX_PAGE_SIZE, postCount).execute(function);
    }

    private void createPhoto(BlocksPost post, Content content) {
        Photo photoEntity = new Photo();
        photoEntity.setPostId(post.getId());
        photoEntity.setTags(post.getTags().toArray(new String[0]));
        photoEntity.setUrls(content.getMedia().stream()
                .map(s -> new PhotoUrl(s.getWidth(), s.getHeight(), s.getUrl()))
                .collect(toList()));
        photoRepository.save(photoEntity);
    }

    private boolean notSkipped(BlocksPost post) {
        return !isSynced(post) && !isArchived(post.getId());
    }

    private boolean isSynced(BlocksPost post) {
        return photoRepository.existsByPostId(post.getId());
    }

    private boolean isArchived(Long postId) {
        return photoArchiveRepository.getPostIds().stream()
                .map(Sets::newHashSet)
                .flatMap(Collection::stream)
                .map(Long::valueOf)
                .anyMatch(e -> e.equals(postId));
    }

}
