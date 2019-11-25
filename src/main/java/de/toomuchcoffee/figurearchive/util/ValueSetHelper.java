package de.toomuchcoffee.figurearchive.util;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class ValueSetHelper {

    public static <T> void add(HasValue<? extends ValueChangeEvent<Set<T>>, Set<T>> obj, T value) {
        Set<T> set = newHashSet(obj.getValue());
        set.add(value);
        obj.setValue(set);
    }

    public static <T> void remove(HasValue<? extends ValueChangeEvent<Set<T>>, Set<T>> obj, T value) {
        Set<T> set = newHashSet(obj.getValue());
        set.remove(value);
        obj.setValue(set);
    }

}
