package com.vaddya.ngram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NGramTokenizer {
    private final int ngram;

    public NGramTokenizer(int ngram) {
        this.ngram = ngram;
    }

    public Collection<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < text.length() - ngram + 1; i++) {
            tokens.add(text.substring(i, i + ngram));

            // heavy useless work
//            for (int j = 0; j < i; j++) {
//                text.substring(j, i);
//            }
        }
        return tokens;
    }
}
