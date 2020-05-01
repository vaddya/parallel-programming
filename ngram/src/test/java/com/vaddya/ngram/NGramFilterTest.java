package com.vaddya.ngram;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static com.vaddya.ngram.NGramCleaner.Options.*;
import static com.vaddya.ngram.NGramTransformer.Options.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NGramFilterTest {
    private static final String SOURCE =
            "— Нет, вы знаете ли, что этот Анатоль мне стоит 40.000 в год, — сказал он.";
    private static final String EXPECTED =
            "нет вы знаете ли что этот анатоль мне стоит в год сказал он";

    private final NGramCleaner cleaner = new NGramCleaner(EnumSet.of(
            REMOVE_LATIN,
            REMOVE_DIGIT,
            REMOVE_PUNCTUATION
    ));
    private final NGramTransformer transformer = new NGramTransformer(EnumSet.of(
            TO_LOWER_CASE,
            SQUASH_SPACES
    ));

    @Test
    void testFilter() {
        String cleaned = cleaner.filter(SOURCE);
        String transformed = transformer.filter(cleaned);
        assertEquals(EXPECTED, transformed);
    }
}