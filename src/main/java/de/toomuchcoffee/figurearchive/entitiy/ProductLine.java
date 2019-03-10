package de.toomuchcoffee.figurearchive.entitiy;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductLine {
    KENNER("Vintage Kenner"),
    POTF2("Power of the Force 2"),
    POTJ("Power of the Jedi");

    private final String description;
}

