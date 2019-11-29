package de.toomuchcoffee.figurearchive.entity;

import lombok.*;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.shingle.ShingleFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;
import org.hibernate.search.bridge.StringBridge;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Indexed
@AnalyzerDef(
        name = "customanalyzer",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = ShingleFilterFactory.class)
        }
)
@Entity
public class Figure implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Field
    @Analyzer(definition = "customanalyzer")
    private String verbatim;
    @Field(bridge = @FieldBridge(impl = EnumStringBridge.class))
    @Enumerated(EnumType.STRING)
    private ProductLine productLine;
    @Field
    private String placementNo;
    @Column(name = "year_released")
    private Short year;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {MERGE })
    @JoinTable(
            name = "figure_to_photo",
            joinColumns = {@JoinColumn(name = "figure_id")},
            inverseJoinColumns = {@JoinColumn(name = "photo_id")}
    )
    private Set<Photo> photos = new HashSet<>();

    public static class EnumStringBridge implements StringBridge {
        @Override
        public String objectToString(Object o) {
            if (o instanceof ProductLine) {
                ProductLine line = (ProductLine) o;
                return line.name() + " " + line.getDescription();
            }
            return (String) o;
        }
    }
}
