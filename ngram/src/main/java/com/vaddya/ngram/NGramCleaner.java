package com.vaddya.ngram;

import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class NGramCleaner implements NGramFilter {
    private static final String LATIN_PATTERN = "[A-Za-zÀ-ÿ]";
    private static final String DIGIT_PATTERN = "[0-9]";
    private static final String PUNCTUATION_PATTERN = "[`,;.!?—\\-\\_\n\t\r\\[\\]«»'…*\\(\\)]";
    private static final String SPACE_PATTERN = " ";

    public enum Options {
        REMOVE_LATIN(LATIN_PATTERN),
        REMOVE_DIGIT(DIGIT_PATTERN),
        REMOVE_PUNCTUATION(PUNCTUATION_PATTERN),
        REMOVE_SPACE(SPACE_PATTERN);

        private final String pattern;

        Options(final String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }
    }

    private final Set<Options> options;

    public NGramCleaner() {
        this(EnumSet.allOf(Options.class));
    }

    public NGramCleaner(@NotNull final Set<Options> options) {
        this.options = options;
    }

    @Override
    @NotNull
    public String filter(@NotNull final String text) {
        if (options.isEmpty()) {
            return text;
        }
        String regex = options
                .stream()
                .map(Options::getPattern)
                .collect(Collectors.joining("|", "(", ")"));
        return text.replaceAll(regex, "");
    }
}
