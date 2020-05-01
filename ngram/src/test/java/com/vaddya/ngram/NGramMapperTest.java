package com.vaddya.ngram;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NGramMapperTest {
    private static final String SOURCE = "Абв, гаБв вбабВ! АВбва - АБВ?";

    private NGramMapper mapper;

    @BeforeEach
    void setUp() {
        final NGramCleaner cleaner = new NGramCleaner(EnumSet.allOf(NGramCleaner.Options.class));
        final NGramTransformer transformer = new NGramTransformer(EnumSet.allOf(NGramTransformer.Options.class));
        final NGramTokenizer tokenizer = new NGramTokenizer(3);
        mapper = new NGramMapper(cleaner, transformer, tokenizer);
    }

    @Test
    void testMap() {
        final Scanner input = new Scanner(SOURCE);
        final Map<String, Integer> map = mapper.map(input);
        assertEquals(4, map.get("абв").intValue());
    }
}