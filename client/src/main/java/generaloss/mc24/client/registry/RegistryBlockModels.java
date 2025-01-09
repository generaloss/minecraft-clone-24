package generaloss.mc24.client.registry;

import generaloss.mc24.client.block.BlockModel;
import generaloss.mc24.client.resourcepack.ResourceBlockModel;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.ResourceRegistry;
import generaloss.mc24.server.resourcepack.ResourcePackManager;

public class RegistryBlockModels extends ResourceRegistry<BlockState, ResourceBlockModel> {

    public BlockModel get(BlockState ID) {
        return super.getResource(ID).getObject();
    }

    public ResourceBlockModel register(BlockState blockstate, ResourcePackManager resourcePackManager) {
        final String blockID = blockstate.getBlockID();
        return super.registerResource(new ResourceBlockModel(
            "blocks/" + blockID + ".json",
            blockstate,
            resourcePackManager
        ));
    }

}
