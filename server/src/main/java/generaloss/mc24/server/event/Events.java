package generaloss.mc24.server.event;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.column.ChunkColumn;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Events {

    private final static List<BlockStateChangedEvent> blockstateChanged = new CopyOnWriteArrayList<>();

    public static void registerBlockstateChanged(BlockStateChangedEvent callback) {
        blockstateChanged.add(callback);
    }

    public static void unregisterBlockstateChanged(BlockStateChangedEvent callback) {
        blockstateChanged.remove(callback);
    }

    public static void invokeBlockstateChanged(Chunk chunk, int x, int y, int z, BlockState state) {
        for(BlockStateChangedEvent callback: blockstateChanged)
            callback.invoke(chunk, x, y, z, state);
    }


    private final static List<BlockLightChangedEvent> blockLightChanged = new CopyOnWriteArrayList<>();

    public static void registerBlockLightChanged(BlockLightChangedEvent callback) {
        blockLightChanged.add(callback);
    }

    public static void unregisterBlockLightChanged(BlockLightChangedEvent callback) {
        blockLightChanged.remove(callback);
    }

    public static void invokeBlockLightChanged(Chunk chunk, int x, int y, int z, int r, int g, int b) {
        for(BlockLightChangedEvent callback: blockLightChanged)
            callback.invoke(chunk, x, y, z, r, g, b);
    }


    private final static List<SkyLightChangedEvent> skyLightChanged = new CopyOnWriteArrayList<>();

    public static void registerSkyLightChanged(SkyLightChangedEvent callback) {
        skyLightChanged.add(callback);
    }

    public static void unregisterSkyLightChanged(SkyLightChangedEvent callback) {
        skyLightChanged.remove(callback);
    }

    public static void invokeSkyLightChanged(ChunkColumn<?> column, int x, int z, int lowY, int highY, int level) {
        for(SkyLightChangedEvent callback: skyLightChanged)
            callback.invoke(column, x, z, lowY, highY, level);
    }

}
