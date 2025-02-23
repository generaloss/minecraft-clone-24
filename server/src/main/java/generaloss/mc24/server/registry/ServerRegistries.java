package generaloss.mc24.server.registry;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.resources.handle.BlockHandle;
import generaloss.mc24.server.resources.pack.ResourcePackManager;
import jpize.util.res.handle.ResHandleMap;
import jpize.util.time.Stopwatch;

public class ServerRegistries {

    public static ResHandleMap<String, BlockHandle> BLOCK;
    public static RegistryBlockState BLOCK_STATE;

    public static void init(ResourcePackManager packManager) {
        BLOCK = new ResHandleMap<>(packManager, (String key, String path) -> new BlockHandle(key, path)); // don't change lambda
        BLOCK_STATE = new RegistryBlockState();

        // default blocks
        Block.AIR.states().create();
        Block.VOID.states().create();
        BLOCK.create(new BlockHandle(Block.AIR));
        BLOCK.create(new BlockHandle(Block.VOID));
    }

    public static void reloadResources() {
        final Stopwatch stopwatch = new Stopwatch().start();
        BLOCK.reload();
        System.out.println("[INFO]: Reloaded server resources in " + stopwatch.getMillis() + " ms.");
    }

}
