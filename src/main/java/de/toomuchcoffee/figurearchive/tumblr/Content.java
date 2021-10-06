package de.toomuchcoffee.figurearchive.tumblr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Content {
    private String type;
    private List<Media> media;
}
