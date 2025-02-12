package generaloss.mc24.server.event;

import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.world.World;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Events {

    private final static List<BlockStateChangedEvent<World<?>, Chunk<?>>> blockstateChanged = new CopyOnWriteArrayList<>();

    public static void registerBlockstateChanged(BlockStateChangedEvent<World<?>, Chunk<?>> callback) {
        blockstateChanged.add(callback);
    }

    public static void unregisterBlockstateChanged(BlockStateChangedEvent<World<?>, Chunk<?>> callback) {
        blockstateChanged.remove(callback);
    }

    public static void invokeBlockstateChanged(Chunk<?> chunk, int x, int y, int z, BlockState state) {
        for(BlockStateChangedEvent<World<?>, Chunk<?>> callback: blockstateChanged)
            callback.invoke(chunk, x, y, z, state);
    }


    private final static List<BlockLightChangedEvent<World<?>, Chunk<?>>> blockLightChanged = new CopyOnWriteArrayList<>();

    public static void registerBlockLightChanged(BlockLightChangedEvent<World<?>, Chunk<?>> callback) {
        blockLightChanged.add(callback);
    }

    public static void unregisterBlockLightChanged(BlockLightChangedEvent<World<?>, Chunk<?>> callback) {
        blockLightChanged.remove(callback);
    }

    public static void invokeBlockLightChanged(Chunk<?> chunk, int x, int y, int z, int r, int g, int b) {
        for(BlockLightChangedEvent<World<?>, Chunk<?>> callback: blockLightChanged)
            callback.invoke(chunk, x, y, z, r, g, b);
    }


    private static final List<BlockLightIncreasedCallback<World<?>, Chunk<?>>> blockLightIncreasedCallbacks = new CopyOnWriteArrayList<>();

    public static void registerLightIncreased(BlockLightIncreasedCallback<World<?>, Chunk<?>> callback) {
        blockLightIncreasedCallbacks.add(callback);
    }

    public static void unregisterLightIncreased(BlockLightIncreasedCallback<World<?>, Chunk<?>> callback) {
        blockLightIncreasedCallbacks.remove(callback);
    }

    public static void invokeLightIncreased(Chunk<?> chunk, int x, int y, int z, int r, int g, int b) {
        for(BlockLightIncreasedCallback<World<?>, Chunk<?>> callback: blockLightIncreasedCallbacks)
            callback.invoke(chunk, x, y, z, r, g, b);
    }

}
