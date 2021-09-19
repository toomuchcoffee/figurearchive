package de.toomuchcoffee.figurearchive.util;

import de.toomuchcoffee.figurearchive.entity.Figure;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

public class FigureDisplayNameHelper {
    public static String getDisplayName(Figure figure) {
        List<String> parts = newArrayList(figure.getVerbatim());
        Optional.ofNullable(figure.getProductLine())
                .ifPresent(parts::add);
        Optional.ofNullable(figure.getPlacementNo())
                .filter(StringUtils::isNotBlank)
                .ifPresent(parts::add);
        return String.join(", ", parts);
    }
}
