package generaloss.mc24.client.resourcepack;

import generaloss.mc24.client.block.BlockStateModel;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.RegistryBlocks;
import generaloss.mc24.server.resourcepack.ResourceHandle;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.util.res.Resource;

import java.util.Collection;

public class ResourceBlockStateModel extends ResourceHandle<BlockState, BlockStateModel> {

    private final BlockStateModel model;
    private final RegistryBlocks BLOCKS;

    public ResourceBlockStateModel(String path, RegistryBlocks BLOCKS) {
        super(path);
        this.model = new BlockStateModel();
        this.BLOCKS = BLOCKS;
    }

    @Override
    public BlockState getID() {
        return model.getBlockState();
    }

    @Override
    public BlockStateModel getObject() {
        return model;
    }

    @Override
    public void load(ResourcePack pack) {
        final Resource resource = pack.getResource(super.getPath());
        model.loadFromJSON(resource.readString(), BLOCKS);
        // System.out.println("Loaded model of block '" + model.getBlockState().getBlockID() + "' for state '" + registries.BLOCK_STATES.getID(model.getBlockState()) + "'");
    }

    @Override
    public void reload(Collection<ResourcePack> packs) {
        final Resource resource = super.getResourceFromPacks(packs, super.getPath());
        model.loadFromJSON(resource.readString(), BLOCKS);
        // System.out.println("Reloaded model of block '" + model.getBlockState().getBlockID() + "' for state '" + registries.BLOCK_STATES.getID(model.getBlockState()) + "'");
    }

    @Override
    public void dispose() { }

}