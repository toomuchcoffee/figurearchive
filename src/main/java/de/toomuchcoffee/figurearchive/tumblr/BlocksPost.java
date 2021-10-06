package de.toomuchcoffee.figurearchive.tumblr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlocksPost {
    private Long id;
    private String type;
    private List<String> tags;
    private List<Content> content;
}
