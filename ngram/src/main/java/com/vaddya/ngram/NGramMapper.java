package com.vaddya.ngram;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class NGramMapper {
    private final NGramCleaner cleaner;
    private final NGramTransformer transformer;
    private final NGramTokenizer tokenizer;

    public NGramMapper(@NotNull final NGramCleaner cleaner,
                       @NotNull final NGramTransformer transformer,
                       @NotNull final NGramTokenizer tokenizer) {
        this.cleaner = cleaner;
        this.transformer = transformer;
        this.tokenizer = tokenizer;
    }

    @NotNull
    public Map<String, Integer> map(@NotNull final Scanner input) {
        final Map<String, Integer> counts = new HashMap<>();
        while (input.hasNext()) {
            String line = input.nextLine();
            String cleaned = cleaner.filter(line);
            String transformed = transformer.filter(cleaned);
            Collection<String> tokens = tokenizer.tokenize(transformed);
            for (String token : tokens) {
                counts.merge(token, 1, Integer::sum);
            }
        }
        return counts;
    }
}
