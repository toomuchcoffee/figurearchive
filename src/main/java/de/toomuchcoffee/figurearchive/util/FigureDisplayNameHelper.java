package de.toomuchcoffee.figurearchive.util;

import de.toomuchcoffee.figurearchive.entity.Figure;

import java.util.Optional;

public class FigureDisplayNameHelper {
    public static String getDisplayName(Figure figure) {
        return Optional.ofNullable(figure.getProductLine())
                .map(l -> String.format("%s (%s)", figure.getVerbatim(), l.name()))
                .orElse(figure.getVerbatim());
    }
}
