package de.toomuchcoffee.figurearchive.event;

import de.toomuchcoffee.figurearchive.entity.Photo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PhotoChangedEvent implements EntityChangedEvent<Photo> {
    private final Photo value;
    private final Operation operation;
}
