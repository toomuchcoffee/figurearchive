package de.toomuchcoffee.figurearchive.event;

import de.toomuchcoffee.figurearchive.entity.Figure;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static de.toomuchcoffee.figurearchive.event.EntityChangedEvent.Operation.CREATED;

@RequiredArgsConstructor
@Getter
public class FigureImportEvent implements EntityChangedEvent<List<Figure>> {
    private final List<Figure> value;
    private final Operation operation = CREATED;
}
