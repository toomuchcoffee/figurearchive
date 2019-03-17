package de.toomuchcoffee.figurearchive.entitiy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Figure {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;
    private String verbatim;
    @Enumerated(EnumType.STRING)
    private ProductLine productLine;
    private Integer year;
    private String image;
}
