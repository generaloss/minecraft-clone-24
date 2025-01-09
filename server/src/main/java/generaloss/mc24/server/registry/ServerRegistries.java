package generaloss.mc24.server.registry;

import generaloss.mc24.server.Facing;
import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.StateProperty;
import generaloss.mc24.server.resourcepack.ResourceBlock;
import generaloss.mc24.server.resourcepack.ResourcePackManager;
import jpize.util.time.Stopwatch;

public class ServerRegistries {

    private static ResourcePackManager packManager;

    public static final RegistryStateProperty STATE_PROPERTY = new RegistryStateProperty();
    public static final RegistryBlocks BLOCK = new RegistryBlocks();
    public static final RegistryBlockState BLOCK_STATE = new RegistryBlockState();

    public static void init(ResourcePackManager packManager) {
        ServerRegistries.packManager = packManager;

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
        BLOCK.register(new ResourceBlock(Block.AIR));
        BLOCK.register(new ResourceBlock(Block.VOID));
    }

    public static void loadResources() {
        final Stopwatch stopwatch = new Stopwatch().start();
        BLOCK.load(packManager.getCorePack());
        System.out.println("[INFO]: Loaded server resources in " + stopwatch.getMillis() + " ms.");
    }

    public static void reloadResources() {
        final Stopwatch stopwatch = new Stopwatch().start();
        BLOCK.reload(packManager.getActivePacks());
        System.out.println("[INFO]: Reloaded server resources in " + stopwatch.getMillis() + " ms.");
    }

}
