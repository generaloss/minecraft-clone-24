package generaloss.mc24.client.resourcepack;

import generaloss.mc24.client.block.BlockStateModel;
import generaloss.mc24.client.registry.ClientRegistries;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.resourcepack.ResourceHandle;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.util.res.Resource;

import java.util.Collection;

public class ResourceBlockStateModel extends ResourceHandle<BlockState, BlockStateModel> {

    private final ClientRegistries registries;
    private final BlockStateModel model;

    public ResourceBlockStateModel(String path, ClientRegistries registries) {
        super(path);
        this.registries = registries;
        this.model = new BlockStateModel();
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
        final Resource resource = pack.get(super.getPath());
        model.loadFromJSON(resource.readString(), registries);
        System.out.println("Loaded model of block '" + model.getBlockState().getBlockID() + "' for state '" + registries.getBlockStateID(model.getBlockState()) + "'");
    }

    @Override
    public void reload(Collection<ResourcePack> packs) {
        final Resource resource = super.getResourceFromPacks(packs, super.getPath());
        model.loadFromJSON(resource.readString(), registries);
        System.out.println("Reloaded model of block '" + model.getBlockState().getBlockID() + "' for state '" + registries.getBlockStateID(model.getBlockState()) + "'");
    }

    @Override
    public void dispose() { }

}