package de.toomuchcoffee.figurearchive.service;

import com.google.common.collect.Lists;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoSize;
import com.tumblr.jumblr.types.Post;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.collect.Sets.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class TumblrService {

    private TumblrService(
            @Value("${tumblr.consumer-key}") String consumerKey,
            @Value("${tumblr.consumer-secret}") String consumerSecret) {
        jumblrClient = new JumblrClient(consumerKey, consumerSecret);
    }

    private final JumblrClient jumblrClient;

    private List<PhotoPost> posts;

    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 8);

    public List<PhotoPost> getPosts(Set<String> filter) {
        return posts.stream()
                .filter(tp -> tp.getTags() != null)
                .filter(tp -> intersection(filter, tp.getTags()).size() > 0)
                .sorted(comparing(tp -> difference(tp.getTags(), filter).size()))
                .sorted(comparing(tp -> intersection(filter, ((PhotoPost) tp).getTags()).size()).reversed())
                .collect(toList());
    }

    @PostConstruct
    private void initialize() throws IOException {
        readPosts();
    }

    public void readPosts() {
        Blog blog = jumblrClient.blogInfo("yaswb.tumblr.com");
        Integer postCount = blog.getPostCount();

        posts = Lists.newArrayList();

        int count = 0;
        int size = 100;
        while ((count * size) < postCount) {
            Map<String, Integer> options = new HashMap<>();
            options.put("limit", size);
            options.put("offset", count * size);
            executor.submit(() -> {
                List<Post> posts = jumblrClient.blogPosts("yaswb", options);
                List<PhotoPost> tumblrPosts = posts.stream()
                        .map(this::extractPhotoPosts)
                        .flatMap(Collection::stream)
                        .collect(toList());
                this.posts.addAll(tumblrPosts);
            });
            count++;
        }
    }

    private Set<PhotoPost> extractPhotoPosts(Post post) {
        if (post instanceof com.tumblr.jumblr.types.PhotoPost) {
            List<Photo> photos = ((com.tumblr.jumblr.types.PhotoPost) post).getPhotos();
            return photos.stream()
                    .map(photo -> extractPhotoPost(post, photo))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(toSet());

        }
        return Collections.emptySet();
    }

    private Optional<PhotoPost> extractPhotoPost(Post post, Photo photo) {
        return photo.getSizes().stream()
                .filter(size -> size.getWidth() == 75)
                .min(comparing(PhotoSize::getHeight))
                .map(photoSize -> {
                    PhotoPost photoPost = new PhotoPost();
                    photoPost.setThumbnail(photoSize.getUrl());
                    photoPost.setPostId(post.getId());
                    photoPost.setTags(newHashSet(post.getTags()));
                    return Optional.of(photoPost);
                })
                .orElse(Optional.empty());
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class PhotoPost {
        private Long postId;
        private Set<String> tags;
        private String thumbnail;
    }

}
