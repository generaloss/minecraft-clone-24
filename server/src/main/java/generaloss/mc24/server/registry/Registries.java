package generaloss.mc24.server.registry;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockProperty;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.resourcepack.ResourceBlock;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.util.time.Stopwatch;

import java.util.Collection;
import java.util.Map;

public class Registries {

    private final ResourcePack defaultPack;
    private final ResourceRegistry<String, ResourceBlock> block;
    private final IntRegistry<BlockState> blockState;

    public Registries(ResourcePack defaultPack) {
        this.defaultPack = defaultPack;

        this.block = new ResourceRegistry<>();
        this.blockState = new IntRegistry<>();

        // register air block
        final Block airBlock = new Block()
                .setID("air")
                .buildStates(Map.of(), this);
        airBlock.properties().set(BlockProperty.OPACITY, 0);
        block.register(new ResourceBlock(airBlock));
        System.out.println("Registered 'air' Block and BlockState with ID " + blockState.getID(airBlock.getDefaultState()));
    }

    public ResourcePack getDefaultPack() {
        return defaultPack;
    }


    public Block getBlock(String ID) {
        return block.get(ID).getObject();
    }

    public ResourceBlock registerBlock(String path) {
        return block.register(new ResourceBlock(path, this));
    }

    public ResourceBlock registerBlock(ResourceBlock blockResource) {
        return block.register(blockResource);
    }


    public BlockState registerBlockState(BlockState state) {
        return blockState.register(state);
    }

    public BlockState getBlockState(int ID) {
        return blockState.get(ID);
    }

    public int getBlockStateID(BlockState state) {
        return blockState.getID(state);
    }


    public void loadResources(ResourcePack defaultPack) {
        final Stopwatch stopwatch = new Stopwatch().start();
        block.load(defaultPack);
        System.out.println("[Server] Loaded resources in " + stopwatch.getMillis() + " ms.");
    }

    public void reloadResources(Collection<ResourcePack> packs) {
        final Stopwatch stopwatch = new Stopwatch().start();
        block.reload(packs);
        System.out.println("[Server] Reloaded resources in " + stopwatch.getMillis() + " ms.");
    }

    public void loadResources() {
        this.loadResources(defaultPack);
    }

}
