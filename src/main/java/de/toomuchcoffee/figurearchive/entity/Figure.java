package de.toomuchcoffee.figurearchive.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Figure {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String verbatim;
    @Enumerated(EnumType.STRING)
    private ProductLine productLine;
    private String placementNo;
    @Column(name = "year_released")
    private Short year;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {MERGE, REMOVE, REFRESH, DETACH})
    @JoinTable(
            name = "figure_to_photo",
            joinColumns = { @JoinColumn(name = "figure_id") },
            inverseJoinColumns = { @JoinColumn(name = "photo_id") }
    )
    private Set<Photo> photos = new HashSet<>();
}
