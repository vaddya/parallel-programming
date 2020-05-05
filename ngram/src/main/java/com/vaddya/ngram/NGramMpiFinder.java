package com.vaddya.ngram;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mpi.MPI;
import mpi.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.*;

public class NGramMpiFinder implements NGramFinder {
    private static final Logger log = LogManager.getLogger(NGramMpiFinder.class);

    private static final int BUFFER_SIZE = 10 * 1024 * 1024; // 10KB
    private static final TypeReference<Map<String, Integer>> STR_INT_MAP = new TypeReference<>() {};

    private final NGramMapper mapper;
    private final NGramReducer reducer;
    private final ObjectMapper json = new ObjectMapper();

    public NGramMpiFinder(
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
                log.info("[{}] finished {} in {} ms", rank, args[i], time);
            }
            final Map<String, Integer> result = reducer.reduce(fileMaps);
            log.info("[{}] finished all", rank);

            if (rank == 0) {
                final ByteBuffer[] buffers = new ByteBuffer[size - 1];
                final Request[] requests = new Request[size - 1];
                for (int i = 1; i < size; i++) {
                    buffers[i - 1] = ByteBuffer.allocateDirect(BUFFER_SIZE);
                    requests[i - 1] = MPI.COMM_WORLD.iRecv(buffers[i - 1], BUFFER_SIZE, MPI.BYTE, i, 0);
                }

                log.info("[{}] Awaiting {} requests", rank, requests.length);
                Request.waitAll(requests);

                final List<Map<String, Integer>> resultMaps = new ArrayList<>(size);
                resultMaps.add(result);
                for (final ByteBuffer buffer : buffers) {
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    resultMaps.add(json.readValue(bytes, STR_INT_MAP));
                }
                return reducer.reduce(resultMaps);
            } else {
                final byte[] bytes = json.writeValueAsBytes(result);
                MPI.COMM_WORLD.send(bytes, bytes.length, MPI.BYTE, 0, 0);
                return Collections.emptyMap();
            }
        } catch (Exception e) {
            log.error("Error occurred", e);
            return Collections.emptyMap();
        } finally {
            MPI.Finalize();
        }
    }
}
