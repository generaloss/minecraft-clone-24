package generaloss.mc24.server.registry;

import generaloss.mc24.server.Facing;
import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.StateProperty;
import generaloss.mc24.server.resourcepack.BlockHandle;
import generaloss.mc24.server.resourcepack.ResourcePackManager;
import jpize.util.res.handle.ResHandleMap;
import jpize.util.time.Stopwatch;

public class ServerRegistries {

    public static RegistryStateProperty STATE_PROPERTY;
    public static ResHandleMap<String, BlockHandle> BLOCK;
    public static RegistryBlockState BLOCK_STATE;

    public static void init(ResourcePackManager packManager) {
        STATE_PROPERTY = new RegistryStateProperty();
        BLOCK = new ResHandleMap<>(packManager, (String key, String path) -> new BlockHandle(key, path));
        BLOCK_STATE = new RegistryBlockState();

        // STATE_PROPERTIES
        STATE_PROPERTY.register(new StateProperty<>("snowy", Boolean.class));
        STATE_PROPERTY.register(new StateProperty<>("lit", Boolean.class));
        STATE_PROPERTY.register(new StateProperty<>("facing", Facing.class));
        STATE_PROPERTY.register(new StateProperty<>("half", String.class, "bottom", "top"));
        STATE_PROPERTY.register(new StateProperty<>("shape", String.class,
            "inner_left", "inner_right", "outer_left", "outer_right", "straight"));
        STATE_PROPERTY.register(new StateProperty<>("bites", Integer.class, 0, 1, 2, 3, 4));

        // BLOCKS
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
