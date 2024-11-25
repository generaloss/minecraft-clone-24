package generaloss.mc24.client.registry;

import generaloss.mc24.client.block.BlockModel;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.registry.Registry;

public class ClientRegistries extends Registries {

    public final Registry<BlockState, BlockModel> BLOCK_MODEL = new Registry<>();

}
