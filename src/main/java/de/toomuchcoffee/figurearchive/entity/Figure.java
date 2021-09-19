package de.toomuchcoffee.figurearchive.entity;

import lombok.*;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.shingle.ShingleFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

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
    @Field
    private String productLine;
    @Field
    private String placementNo;
    @Column(name = "year_released")
    private Short year;
    @IndexedEmbedded
    @Column(name = "figure_count")
    private int count;
}
