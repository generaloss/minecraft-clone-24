package generaloss.mc24.server.chunk;

public class ChunkStorage {

    private final ChunkByteArray blockstates;
    private final ChunkMultiByteArray blocklight;
    private final ChunkMultiByteArray skylight;

    public ChunkStorage(ChunkByteArray blockstates, ChunkMultiByteArray blocklight, ChunkMultiByteArray skylight) {
        this.blockstates = blockstates;
        this.blocklight = blocklight;
        this.skylight = skylight;
    }

    public ChunkStorage(boolean hasSkylight) {
        this(
            new ChunkByteArray(),
            new ChunkMultiByteArray(3),
            (hasSkylight ? new ChunkMultiByteArray(3) : null)
        );
    }


    public ChunkByteArray blockstates() {
        return blockstates;
    }

    public ChunkMultiByteArray blocklight() {
        return blocklight;
    }

    public ChunkMultiByteArray skylight() {
        return skylight;
    }


    public boolean hasSkylight() {
        return (skylight != null);
    }

}
