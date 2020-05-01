package com.vaddya.ngram;

import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

public class NGramTransformer implements NGramFilter {
    private static final String SPACES = "[ \n\t]+";

    public enum Options {
        TO_LOWER_CASE,
        SQUASH_SPACES
    }

    public NGramTransformer() {
        this(EnumSet.allOf(Options.class));
    }

    public NGramTransformer(Set<Options> options) {
        this.options = options;
    }
    
    private final Set<Options> options;
    
    @Override
    @NotNull
    public String filter(@NotNull String text) {
        if (options.contains(Options.TO_LOWER_CASE)) {
            text = text.toLowerCase();
        }
        if (options.contains(Options.SQUASH_SPACES)) {
            text = text.replaceAll(SPACES, " ").strip();
        }
        return text;
    }
}
