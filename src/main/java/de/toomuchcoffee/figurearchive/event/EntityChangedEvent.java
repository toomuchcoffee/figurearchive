package de.toomuchcoffee.figurearchive.event;

public interface EntityChangedEvent<T> {
    T getValue();
    Operation getOperation();

    public enum Operation {
        CREATED, UPDATED, DELETED
    }
}
