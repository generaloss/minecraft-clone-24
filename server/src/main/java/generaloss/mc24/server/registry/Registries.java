package generaloss.mc24.server.registry;

import generaloss.mc24.server.IntRegistry;
import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.resourcepack.ResourceBlock;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.util.time.Stopwatch;

import java.util.Collection;
import java.util.Map;

public class Registries {

    private final ResourcePack defaultPack;

    public final RegistryBlocks BLOCKS;
    public final IntRegistry<BlockState> BLOCK_STATES;

    public Registries(ResourcePack defaultPack) {
        this.defaultPack = defaultPack;

        this.BLOCKS = new RegistryBlocks();
        this.BLOCK_STATES = new IntRegistry<>();

        // register core blocks
        Block.AIR.createBlockstates(Map.of(), this);
        Block.VOID.createBlockstates(Map.of(), this);
        BLOCKS.register(new ResourceBlock(Block.AIR));
        BLOCKS.register(new ResourceBlock(Block.VOID));
        // System.out.println("Registered 'air' Block and BlockState with ID " + BLOCK_STATES.getID(airBlock.getDefaultState()));
    }

    public ResourcePack getDefaultPack() {
        return defaultPack;
    }

    public void loadResources(ResourcePack defaultPack) {
        final Stopwatch stopwatch = new Stopwatch().start();
        BLOCKS.load(defaultPack);
        System.out.println("[INFO]: Loaded server resources in " + stopwatch.getMillis() + " ms.");
    }

    public void reloadResources(Collection<ResourcePack> packs) {
        final Stopwatch stopwatch = new Stopwatch().start();
        BLOCKS.reload(packs);
        System.out.println("[INFO]: Reloaded server resources in " + stopwatch.getMillis() + " ms.");
    }

    public void loadResources() {
        this.loadResources(defaultPack);
    }

}
