package de.toomuchcoffee.figurearchive.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductLine {
    // inspired by JediBusiness.com
    KENNER("Vintage Kenner"),
    POTF2("Power of the Force 2"),
    SOTE("Shadows of the Empire"),
    EPISODE1("Episode 1"),
    POTJ("Power of the Jedi"),
    SAGA("Saga"),
    DC("Star Tours"),
    OCW("Clone Wars"),
    CWANIMATED("Clone Wars Animated Style"),
    OTC("Original Trilogy Collection"),
    ROTS("Revenge of the Sith"),
    TSC("The Saga Collection"),
    TAC("The Anniversary Collection"),
    TLC("The Legacy Collection"),
    TCW("The Clone Wars"),
    SOTDS("Shadow of the Dark Side"),
    TVC("The Vintage Collection"),
    DTF("Discover the Force"),
    MH("Movie Heroes"),
    TLC13("The Legacy Collection 2013"),
    SL("Saga Legends"),
    TBS("The Black Series"),
    TBS6("The Black Series 6\""),
    SWR("Star Wars Rebels Saga Legends"),
    TFA("The Force Awakens"),
    RO("Rogue One"),
    TLJ("The Last Jedi"),
    SOLO("Solo: A Star Wars Story"),
    GOA("Galaxy of Adventures"),
    RESISTANCE("Star Wars Resistance"),
    DISNEY("All Disney Lines"),
    SHFIGUARTS("SH Figuarts"),
    BANDAI("Bandai Model Kit"),
    HERO("12\" Hero Series");

    private final String description;
}

