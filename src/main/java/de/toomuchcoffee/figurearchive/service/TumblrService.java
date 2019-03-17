package de.toomuchcoffee.figurearchive.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.intersection;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;

@Service
public class TumblrService {

    private List<TumblrPost> posts;

    private Long timestamp;

    private ObjectMapper objectMapper;

    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 8);

    public List<TumblrPost> getPosts(Set<String> filter) {
        return posts.stream()
                .filter(tp -> tp.getTags() != null && intersection(filter, newHashSet(tp.getTags())).size() > 0)
                .collect(toList());
    }

    public TumblrPost getAnyPost() {
        int index = new Random().nextInt(posts.size());
        return posts.get(index);
    }

    @PostConstruct
    private void initialize() throws IOException {
        readPosts();
    }

    public void readPosts() {
        this.timestamp = System.currentTimeMillis();

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String tumblrFirstPageUrl = "http://yaswb.tumblr.com/api/read/json?type=photo";
        TumblrResponse tumblrFirstResponse = getTumblrResponse(tumblrFirstPageUrl);
        posts = newArrayList(tumblrFirstResponse.posts).stream()
                .filter(tp -> tp.tags != null)
                .collect(toList());

        int offset = 20;
        int maxPage = (int) Math.ceil(((double) tumblrFirstResponse.postsTotal) / offset);
        for (int page = 1; page < maxPage; page++) {
            final int start = page * offset;
            final String tumblrUrl = String.format("http://yaswb.tumblr.com/api/read/json?type=photo&num=20&start=%s", start);
            executor.submit(() -> {
                System.out.println(String.format("Getting tumblr response from url: %s", tumblrUrl));
                TumblrResponse tumblrPageResponse = getTumblrResponse(tumblrUrl);
                posts.addAll(Arrays.asList(tumblrPageResponse.posts));
            });
        }
    }

    public Long getTimestamp() {
        return timestamp;
    }

    private TumblrResponse getTumblrResponse(String tumblrUrl) {
        String jsonString = new RestTemplate().getForObject(tumblrUrl, String.class);
        jsonString = jsonString.replaceFirst("var tumblr_api_read =", "").trim();

        try {
            return objectMapper.readValue(jsonString, TumblrResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonIgnoreProperties
    public static class TumblrResponse {
        @JsonProperty("posts-total")
        public int postsTotal;

        public TumblrPost[] posts;
    }

    @JsonIgnoreProperties
    @Getter
    public static class TumblrPost {
        private String[] tags;

        @JsonProperty("photo-url-1280")
        private String photoUrl1280;

        @JsonProperty("photo-url-500")
        private String photoUrl500;

        @JsonProperty("photo-url-400")
        private String photoUrl400;

        @JsonProperty("photo-url-250")
        private String photoUrl250;

        @JsonProperty("photo-url-100")
        private String photoUrl100;

        @JsonProperty("photo-url-75")
        private String photoUrl75;
    }

}
