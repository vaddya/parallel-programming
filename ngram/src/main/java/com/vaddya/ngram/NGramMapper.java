package com.vaddya.ngram;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class NGramMapper {
    private static final Logger log = LogManager.getLogger(NGramMapper.class);
    
    private final NGramCleaner cleaner;
    private final NGramTransformer transformer;
    private final NGramTokenizer tokenizer;

    public NGramMapper(
            @NotNull final NGramCleaner cleaner,
            @NotNull final NGramTransformer transformer,
            @NotNull final NGramTokenizer tokenizer) {
        this.cleaner = cleaner;
        this.transformer = transformer;
        this.tokenizer = tokenizer;
    }

    @NotNull
    public Map<String, Integer> map(@NotNull final Scanner input) {
        final Map<String, Integer> counts = new HashMap<>();
        int tokensCount = 0;
        while (input.hasNext()) {
            String line = input.nextLine();
            String cleaned = cleaner.filter(line);
            String transformed = transformer.filter(cleaned);
            Collection<String> tokens = tokenizer.tokenize(transformed);
            tokensCount += tokens.size();
            for (String token : tokens) {
                counts.merge(token, 1, Integer::sum);
            }
        }
        counts.put("__tokenCount", tokensCount);
        return counts;
    }
}
