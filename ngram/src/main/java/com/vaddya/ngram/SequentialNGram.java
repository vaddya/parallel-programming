package com.vaddya.ngram;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SequentialNGram {
    private final NGramMapper mapper = new NGramMapper(new NGramCleaner(), new NGramTransformer(), new NGramTokenizer(3));
    private final NGramReducer reducer = new NGramReducer();

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
