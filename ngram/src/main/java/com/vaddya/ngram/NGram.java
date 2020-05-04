package com.vaddya.ngram;

import java.util.Map;

public class NGram {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: java -jar ngram.jar true 3 \"1.txt,2.txt\"");
            System.out.println("- Use MPI: boolean flag");
            System.out.println("- Ngram: integer value");
            System.out.println("- Text files: string values separated by \",\"");
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
            System.out.printf("Found %d %d-grams in %d tokens in %d ms\n", result.size(), ngram, tokenCount, time);
            System.out.println("Top 20:");
            Utils.sortedByValues(result)
                    .limit(20)
                    .forEach(e -> {
                        final double prob = e.getValue().doubleValue() / tokenCount;
                        System.out.printf("%s -> %.2f%% (%d)\n", e.getKey(), prob * 100, e.getValue());
                    });
        }
    }

    static NGramFinder createFinder(
            final boolean useMpi,
            final int ngram) {
        System.out.printf("useMpi=%b, ngram=%d\n", useMpi, ngram);
        final NGramCleaner cleaner = new NGramCleaner();
        final NGramTransformer transformer = new NGramTransformer();
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
