package com.vaddya.ngram;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface NGramFinder {
    @NotNull
    Map<String, Integer> findNGrams(String[] files) throws Exception;
}
