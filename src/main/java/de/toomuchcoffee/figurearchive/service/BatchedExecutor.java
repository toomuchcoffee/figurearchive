package de.toomuchcoffee.figurearchive.service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class BatchedExecutor {
    private final int pageSize;
    private final int totalCount;

    public <T> List<T> execute(List<T> collector, BiConsumer<Integer, Integer> function) {
        int count = 0;
        while ((count * pageSize) < totalCount) {
            int offset = count * pageSize;
            function.accept(offset, pageSize);
            count++;
        }
        return collector;
    }
}
