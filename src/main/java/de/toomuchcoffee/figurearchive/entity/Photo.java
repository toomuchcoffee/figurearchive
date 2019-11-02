package de.toomuchcoffee.figurearchive.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Photo {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "post_id")
    private Long postId;

    private String tags;

    @Column(name = "url_1280")
    private String url1280;

    @Column(name = "url_500")
    private String url500;

    @Column(name = "url_400")
    private String url400;

    @Column(name = "url_250")
    private String url250;

    @Column(name = "url_100")
    private String url100;

    @Column(name = "url_75")
    private String url75;
}
