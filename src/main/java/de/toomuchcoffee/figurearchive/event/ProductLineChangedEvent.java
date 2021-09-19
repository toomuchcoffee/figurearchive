package de.toomuchcoffee.figurearchive.event;

import de.toomuchcoffee.figurearchive.entity.ProductLine;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ProductLineChangedEvent implements EntityChangedEvent<ProductLine> {
    private final ProductLine value;
    private final Operation operation;
}
