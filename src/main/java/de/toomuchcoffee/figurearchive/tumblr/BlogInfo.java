package de.toomuchcoffee.figurearchive.tumblr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogInfo {
    @JsonProperty("total_posts")
    private int totalPosts;
}
