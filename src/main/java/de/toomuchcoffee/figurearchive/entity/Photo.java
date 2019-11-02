package de.toomuchcoffee.figurearchive.entity;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonBinaryType.class),
        @TypeDef(name = "string-array", typeClass = StringArrayType.class)
})
public class Photo {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "post_id")
    private Long postId;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private List<PhotoUrl> urls;

    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    private String[] tags;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhotoUrl implements Serializable {
        private Integer width;
        private Integer height;
        private String url;
    }
}
