package de.toomuchcoffee.figurearchive.entity;

import com.vladmihalcea.hibernate.type.array.LongArrayType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@TypeDefs({
        @TypeDef(name = "bigint-array", typeClass = LongArrayType.class)
})
public class PhotoArchive implements Serializable {
    @Id
    private Long id = 1L;

    @Type(type = "bigint-array")
    @Column(columnDefinition = "bigint[]")
    private Long[] postIds;
}
