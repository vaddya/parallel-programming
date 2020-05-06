package com.vaddya.ngram;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.Map;

import static com.vaddya.ngram.NGramCleaner.Options.*;
import static com.vaddya.ngram.NGramTransformer.Options.SQUASH_SPACES;
import static com.vaddya.ngram.NGramTransformer.Options.TO_LOWER_CASE;

public class NGram {
    private static final Logger log = LogManager.getLogger(NGram.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            log.error("Usage: java -jar ngram.jar true 3 \"1.txt,2.txt\"");
            log.error("- Use MPI: boolean flag");
            log.error("- Ngram: integer value");
            log.error("- Text files: string values separated by \",\"");
            System.exit(1);
        }

        final boolean useMpi = Boolean.parseBoolean(args[0]);
        final int ngram = Integer.parseInt(args[1]);
        final String[] files = args[2].split(",");
        final NGramFinder finder = createFinder(useMpi, ngram);

        final long start = System.currentTimeMillis();
        final Map<String, Integer> result = finder.findNGrams(files);
        final long time = System.currentTimeMillis() - start;
        if (result.size() > 0) {
            final int tokenCount = result.remove("__tokenCount");
            log.info("Found {} {}-grams in {} tokens in {} ms", result.size(), ngram, tokenCount, time);
            log.info("Top 20:");
            Utils.sortedByValues(result)
                    .limit(20)
                    .forEach(e -> {
                        final double prob = e.getValue().doubleValue() / tokenCount;
                        log.printf(Level.INFO, "%s -> %.2f%% (%d)", e.getKey(), prob * 100, e.getValue());
                    });
        }
    }

    static NGramFinder createFinder(
            final boolean useMpi,
            final int ngram) {
        log.info("useMpi={}, ngram={}", useMpi, ngram);
        final NGramCleaner cleaner = new NGramCleaner(EnumSet.of(
                REMOVE_LATIN,
                REMOVE_DIGIT,
                REMOVE_SPACE,
                REMOVE_PUNCTUATION
        ));
        final NGramTransformer transformer = new NGramTransformer(EnumSet.of(
                TO_LOWER_CASE,
                SQUASH_SPACES
        ));
        final NGramTokenizer tokenizer = new NGramTokenizer(ngram);
        final NGramMapper mapper = new NGramMapper(cleaner, transformer, tokenizer);
        final NGramReducer reducer = new NGramReducer();
        if (useMpi) {
            return new NGramMpiFinder(mapper, reducer);
        } else {
            return new NGramSeqFinder(mapper, reducer);
        }
    }
}
