package com.vaddya.ngram;

import org.jetbrains.annotations.NotNull;

public interface NGramFilter {
    @NotNull
    String filter(@NotNull String text);
}
