package de.toomuchcoffee.figurearchive.service;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class BatchedExecutorTest {

    @Test
    public void test() {
        int totalCount = 2345;
        List<Integer> source = IntStream.range(0, totalCount).boxed().collect(toList());

        BatchedExecutor batchedExecutor = new BatchedExecutor(100, source.size());

        ArrayList<Object> target = new ArrayList<>();

        BiConsumer<Integer, Integer> f = (offset, pageSize) ->
                target.addAll(source.subList(offset, Math.min(offset + pageSize, totalCount)));

        batchedExecutor.execute(f);

        assertThat(target.size()).isEqualTo(source.size());
    }
}