package de.toomuchcoffee.figurearchive.event;

import de.toomuchcoffee.figurearchive.entity.Figure;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FigureChangedEvent implements EntityChangedEvent<Figure> {
    private final Figure value;
    private final Operation operation;
}
