package com.vaddya.ngram;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NGramReducerTest {
    private final NGramReducer reducer = new NGramReducer();

    @Test
    void reduceTest() {
        final Map<String, Integer> map1 = Map.of("абв", 10, "где", 20);
        final Map<String, Integer> map2 = Map.of("где", 30);
        final Map<String, Integer> map3 = Map.of("жзи", 1);
        final Map<String, Integer> reduced = reducer.reduce(List.of(map1, map2, map3));
        assertEquals(Map.of("абв", 10, "где", 50, "жзи", 1), reduced);
    }
}