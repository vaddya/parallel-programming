package com.vaddya.ngram;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class NGramSeqFinder implements NGramFinder {
    private final NGramMapper mapper;
    private final NGramReducer reducer;

    public NGramSeqFinder(
            @NotNull final NGramMapper mapper,
            @NotNull final NGramReducer reducer) {
        this.mapper = mapper;
        this.reducer = reducer;
    }

    @Override
    @NotNull
    public Map<String, Integer> findNGrams(@NotNull final String[] files) throws FileNotFoundException {
        final List<Map<String, Integer>> maps = new ArrayList<>();
        for (String file : files) {
            try (final Scanner input = new Scanner(new File(file))) {
                maps.add(mapper.map(input));
            }
        }
        return reducer.reduce(maps);
    }
}
