package generaloss.mc24.client.registry;

import generaloss.mc24.client.block.BlockStateModel;
import generaloss.mc24.client.resourcepack.ResourceBlockStateModel;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.RegistryBlocks;
import generaloss.mc24.server.registry.ResourceRegistry;

public class RegistryBlockModels extends ResourceRegistry<BlockState, ResourceBlockStateModel> {

    private final RegistryBlocks BLOCKS;

    public RegistryBlockModels(RegistryBlocks BLOCKS) {
        this.BLOCKS = BLOCKS;
    }


    public BlockStateModel get(BlockState ID) {
        return super.getResource(ID).getObject();
    }

    public ResourceBlockStateModel register(String path) {
        return super.registerResource(new ResourceBlockStateModel(path, BLOCKS));
    }

    public ResourceBlockStateModel register(ResourceBlockStateModel blockstateModelResource) {
        return super.registerResource(blockstateModelResource);
    }

}
