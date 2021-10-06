package de.toomuchcoffee.figurearchive.tumblr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private BlogInfo blog;
    private List<BlocksPost> posts;
}
