package com.vaddya.ngram;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

public class NGram {
    public static void main(String[] args) throws FileNotFoundException {
        final long start = System.currentTimeMillis();
        final SequentialNGram sequential = new SequentialNGram();
        final Map<String, Integer> result = sequential.findNGrams(args);
        final long time = System.currentTimeMillis() - start;
        System.out.println(
                String.format("Found %d 3-grams in %d seconds", result.size(), time / 1000));

        System.out.println("Top 20:");
        sortedByValues(result)
                .limit(20)
                .forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue()));
    }

    static Stream<Map.Entry<String, Integer>> sortedByValues(@NotNull final Map<String, Integer> map) {
        final SortedSet<Map.Entry<String, Integer>> sorted = new TreeSet<>(
                Map.Entry.comparingByValue(Comparator.reverseOrder()));
        sorted.addAll(map.entrySet());
        return sorted.stream();
    }
}
