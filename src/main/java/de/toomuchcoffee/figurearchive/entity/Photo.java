package de.toomuchcoffee.figurearchive.entity;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonBinaryType.class),
        @TypeDef(name = "string-array", typeClass = StringArrayType.class)
})
public class Photo implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Long postId;
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private List<PhotoUrl> urls;
    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    private String[] tags;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {MERGE, REFRESH})
    @JoinTable(
            name = "figure_to_photo",
            joinColumns = {@JoinColumn(name = "photo_id")},
            inverseJoinColumns = {@JoinColumn(name = "figure_id")}
    )
    private Set<Figure> figures = new HashSet<>();
    private boolean completed;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhotoUrl implements Serializable {
        private Integer width;
        private Integer height;
        private String url;
    }
}
