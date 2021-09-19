package de.toomuchcoffee.figurearchive.entity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@EqualsAndHashCode(of = "code")
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductLine {
    @Id
    private String code;
    private String description;
    @Column(name = "year_started")
    private Short year;
}

