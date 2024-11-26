package generaloss.mc24.server.registry;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockProperty;
import generaloss.mc24.server.block.BlockState;

import java.util.Map;

public class Registries {

    private final Registry<String, Block> block;
    private final IntRegistry<BlockState> blockState;

    public Registries() {
        this.block = new Registry<>();
        this.blockState = new IntRegistry<>();

        // register air block
        final Block airBlock = new Block("air", Map.of(), blockState);
        airBlock.properties().set(BlockProperty.OPAQUE_LEVEL, 0);
        block.register("air", airBlock);
        System.out.println("Registered 'air' Block and BlockState with ID " + blockState.getID(airBlock.getDefaultState()));
    }

    public Registry<String, Block> block() {
        return block;
    }

    public IntRegistry<BlockState> blockState() {
        return blockState;
    }
}
