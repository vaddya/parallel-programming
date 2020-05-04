package com.vaddya.ngram;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mpi.MPI;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class MpiNGramFinder implements NGramFinder {
    private static final int BUFFER_SIZE = 10 * 1024 * 1024; // 10KB
    private static final TypeReference<Map<String, Integer>> STR_INT_MAP = new TypeReference<>() {};

    private final NGramMapper mapper;
    private final NGramReducer reducer;
    private final ObjectMapper json = new ObjectMapper();

    public MpiNGramFinder(
            @NotNull final NGramMapper mapper,
            @NotNull final NGramReducer reducer) {
        this.mapper = mapper;
        this.reducer = reducer;
    }

    @Override
    @NotNull
    public Map<String, Integer> findNGrams(@NotNull final String[] args) throws Exception {
        MPI.Init(args);
        try {
            final int rank = MPI.COMM_WORLD.getRank();
            final int size = MPI.COMM_WORLD.getSize();

            final List<Map<String, Integer>> fileMaps = new ArrayList<>();
            for (int i = rank; i < args.length; i += size) {
                final long start = System.currentTimeMillis();
                try (final Scanner input = new Scanner(new File(args[i]))) {
                    fileMaps.add(mapper.map(input));
                }
                final long time = System.currentTimeMillis() - start;
                System.out.printf("%d processed %s in %d ms \n", rank, args[i], time);
            }
            final Map<String, Integer> result = reducer.reduce(fileMaps);

            if (rank == 0) {
                final List<Map<String, Integer>> resultMaps = new ArrayList<>(size);
                resultMaps.add(result);
                final byte[] buffer = new byte[BUFFER_SIZE];
                for (int i = 1; i < size; i++) {
                    MPI.COMM_WORLD.recv(buffer, BUFFER_SIZE, MPI.BYTE, i, 0);
                    resultMaps.add(json.readValue(buffer, STR_INT_MAP));
                    Arrays.fill(buffer, (byte) 0);
                }
                return reducer.reduce(resultMaps);
            } else {
                final byte[] bytes = json.writeValueAsBytes(result);
                MPI.COMM_WORLD.send(bytes, bytes.length, MPI.BYTE, 0, 0);
                return Collections.emptyMap();
            }
        } finally {
            MPI.Finalize();
        }
    }
}
