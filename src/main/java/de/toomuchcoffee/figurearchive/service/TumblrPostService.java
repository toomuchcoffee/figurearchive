package de.toomuchcoffee.figurearchive.service;

import com.google.common.collect.ImmutableMap;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.Post;
import de.toomuchcoffee.figurearchive.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.joining;

@Service
public class TumblrPostService {
    private final JumblrClient jumblrClient;
    private final PhotoRepository photoRepository;

    private final ExecutorService executor = newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 8);

    private TumblrPostService(
            @Value("${tumblr.consumer-key}") String consumerKey,
            @Value("${tumblr.consumer-secret}") String consumerSecret,
            @Autowired PhotoRepository photoRepository) {
        this.jumblrClient = new JumblrClient(consumerKey, consumerSecret);
        this.photoRepository = photoRepository;
    }

    @PostConstruct
    private void initialize() throws IOException {
        readPosts();
    }

    public void readPosts() {
        Blog blog = jumblrClient.blogInfo("yaswb.tumblr.com");
        Integer postCount = blog.getPostCount();

        BiConsumer<Integer, Integer> function = (offset, pageSize) -> {
            Map<String, Integer> options = ImmutableMap.of("limit", pageSize, "offset", offset);
            executor.submit(() ->
                    jumblrClient.blogPosts("yaswb", options).stream()
                            .filter(post -> !photoRepository.existsByPostId(post.getId()))
                            .forEach(this::extractPhotoPosts));
        };

        new BatchedExecutor(100, postCount).execute(newArrayList(), function);
    }

    private void extractPhotoPosts(Post post) {
        if (post instanceof com.tumblr.jumblr.types.PhotoPost) {
            List<Photo> photos = ((com.tumblr.jumblr.types.PhotoPost) post).getPhotos();
            photos.forEach(photo -> extractPhotoPost(post, photo));
        }
    }

    private void extractPhotoPost(Post post, Photo photo) {
        de.toomuchcoffee.figurearchive.entity.Photo photoEntity =
                new de.toomuchcoffee.figurearchive.entity.Photo();
        photo.getSizes().forEach(photoSize -> {
            photoEntity.setPostId(post.getId());
            photoEntity.setTags(post.getTags().stream()
                    .map(String::trim)
                    .collect(joining(",")));

            switch (photoSize.getWidth()) {
                case 1280:
                    photoEntity.setUrl1280(photoSize.getUrl());
                    break;
                case 500:
                    photoEntity.setUrl500(photoSize.getUrl());
                    break;
                case 400:
                    photoEntity.setUrl400(photoSize.getUrl());
                    break;
                case 250:
                    photoEntity.setUrl250(photoSize.getUrl());
                    break;
                case 100:
                    photoEntity.setUrl100(photoSize.getUrl());
                    break;
                case 75:
                    photoEntity.setUrl75(photoSize.getUrl());
                    break;
                default:
                    System.err.println("WOOPS: " + post.getPostUrl()); // TODO
            }
            photoRepository.save(photoEntity);
        });
    }

}
