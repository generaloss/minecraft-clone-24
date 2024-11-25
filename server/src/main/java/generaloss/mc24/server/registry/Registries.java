package generaloss.mc24.server.registry;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockState;

public class Registries {

    public final Registry<String, Block> BLOCK       = new Registry<>();
    public final IntRegistry<BlockState> BLOCK_STATE = new IntRegistry<>();

}
