package de.toomuchcoffee.figurearchive.entitiy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
    private ProductLine productLine;
    private Integer year;
}
