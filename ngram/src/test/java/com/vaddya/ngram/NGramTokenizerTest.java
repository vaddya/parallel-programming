package com.vaddya.ngram;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NGramTokenizerTest {
    private static final String SOURCE =
            "нет вы знаете ли что этот анатоль мне стоит в год сказал он";

    private final NGramTokenizer tokenizer = new NGramTokenizer(3);

    @Test
    void testTokenize() {
        assertEquals(57, tokenizer.tokenize(SOURCE).size());
    }
}