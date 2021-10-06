package de.toomuchcoffee.figurearchive.util;

import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class BatchedExecutor {
    private final int pageSize;
    private final int totalCount;

    public void execute(BiConsumer<Integer, Integer> function) {
        int count = 0;
        while ((count * pageSize) < totalCount) {
            int offset = count * pageSize;
            function.accept(offset, pageSize);
            count++;
        }
    }
}
