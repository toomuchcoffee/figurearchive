package de.toomuchcoffee.figurearchive.service;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import de.toomuchcoffee.figurearchive.tumblr.BlocksPost;
import de.toomuchcoffee.figurearchive.tumblr.BlogInfo;
import de.toomuchcoffee.figurearchive.tumblr.TumblrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@VaadinSessionScope
public class TumblrService {
    private final String consumerKey;
    private final TumblrClient tumblrClient;

    private TumblrService(@Value("${tumblr.consumer-key}") String consumerKey, @Autowired TumblrClient tumblrClient) {
        this.consumerKey = consumerKey;
        this.tumblrClient = tumblrClient;
    }

    public BlogInfo getInfo() {
        return tumblrClient.getInfo(consumerKey).getResponse().getBlog();
    }

    public List<BlocksPost> getPosts(Integer offset, Integer limit) {
        return tumblrClient.getPosts(consumerKey, offset, limit).getResponse().getPosts();
    }
}
