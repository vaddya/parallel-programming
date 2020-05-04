package com.vaddya.ngram;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

public final class Utils {
    private Utils () {
    }

    @NotNull
    public static Stream<Map.Entry<String, Integer>> sortedByValues(@NotNull final Map<String, Integer> map) {
        final SortedSet<Map.Entry<String, Integer>> sorted = new TreeSet<>(
                Map.Entry.comparingByValue(Comparator.reverseOrder()));
        sorted.addAll(map.entrySet());
        return sorted.stream();
    }
}
