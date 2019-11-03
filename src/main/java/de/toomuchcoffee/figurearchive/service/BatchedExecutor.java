package de.toomuchcoffee.figurearchive.service;

import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
class BatchedExecutor {
    private final int pageSize;
    private final int totalCount;

    <T> void execute(BiConsumer<Integer, Integer> function) {
        int count = 0;
        while ((count * pageSize) < totalCount) {
            int offset = count * pageSize;
            function.accept(offset, pageSize);
            count++;
        }
    }
}
