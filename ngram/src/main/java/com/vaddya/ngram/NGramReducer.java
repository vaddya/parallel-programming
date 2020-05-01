package com.vaddya.ngram;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NGramReducer {
    @NotNull
    public Map<String, Integer> reduce(@NotNull final Collection<Map<String, Integer>> maps) {
        final Map<String, Integer> accumulator = new HashMap<>();
        for (final Map<String, Integer> map : maps) {
            for (final Map.Entry<String, Integer> entry : map.entrySet()) {
                accumulator.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }
        return accumulator;
    }
}
