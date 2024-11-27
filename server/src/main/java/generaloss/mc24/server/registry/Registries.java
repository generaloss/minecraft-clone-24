package generaloss.mc24.server.registry;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockProperty;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.resourcepack.ResourceBlock;

import java.util.Map;

public class Registries {

    private final Registry<String, ResourceBlock> block;
    private final IntRegistry<BlockState> blockState;

    public Registries() {
        this.block = new Registry<>();
        this.blockState = new IntRegistry<>();

        // register air block
        final Block airBlock = new Block("air", Map.of(), blockState);
        airBlock.properties().set(BlockProperty.OPAQUE_LEVEL, 0);
        block.register(airBlock);
        System.out.println("Registered 'air' Block and BlockState with ID " + blockState.getID(airBlock.getDefaultState()));
    }

    public ResourceBlock registerBlockModel(String path) {
        return block.register(new ResourceBlock(context.getDefaultPack(), this, path));
    }

    public ResourceBlock registerBlockModel(ResourceBlock blockResource) {
        return block.register(blockResource);
    }

    public IntRegistry<BlockState> blockState() {
        return blockState;
    }
}
