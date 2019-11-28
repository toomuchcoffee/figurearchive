package de.toomuchcoffee.figurearchive.entity;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Indexed
@Entity
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonBinaryType.class),
        @TypeDef(name = "string-array", typeClass = StringArrayType.class)
})
public class Photo {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Long postId;
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private List<PhotoUrl> urls;
    @Type(type = "string-array")
    @Field
    @IndexedEmbedded
    @Column(columnDefinition = "text[]")
    private String[] tags;
    @ManyToMany(mappedBy = "photos", fetch = FetchType.EAGER, cascade = {REMOVE, REFRESH, DETACH})
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
