package de.toomuchcoffee.figurearchive.event;

public interface EntityChangedEvent<T> {
    T getValue();
    Operation getOperation();

    enum Operation {
        CREATED, UPDATED, DELETED
    }
}
