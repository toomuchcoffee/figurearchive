package de.toomuchcoffee.figurearchive.tumblr;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "tumblrClient", url = "${tumblr.url}")
public interface TumblrClient {

    @GetMapping(value = "/v2/blog/yaswb/info")
    ResponseWrapper getInfo(@RequestParam("api_key") String apiKey);

    @GetMapping(value = "/v2/blog/yaswb/posts?npf=true")
    ResponseWrapper getPosts(@RequestParam("api_key") String apiKey, @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit);

}
