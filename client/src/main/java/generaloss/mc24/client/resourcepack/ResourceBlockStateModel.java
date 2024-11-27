package generaloss.mc24.client.resourcepack;

import generaloss.mc24.client.block.BlockStateModel;
import generaloss.mc24.client.registry.ClientRegistries;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.resourcepack.ResourceHandle;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.util.res.Resource;

public class ResourceBlockStateModel extends ResourceHandle<BlockState, BlockStateModel> {

    private final ClientRegistries registries;
    private final BlockStateModel model;

    public ResourceBlockStateModel(ResourcePack defaultPack, ClientRegistries registries, String path) {
        super(defaultPack, path);
        this.registries = registries;
        this.model = new BlockStateModel();
    }

    @Override
    public BlockStateModel object() {
        return model;
    }

    @Override
    public void load(ResourcePack pack) {
        final Resource resource = pack.getOrDefault(super.getPath(), super.getDefaultPack());

        model.loadFromJSON(resource.readString(), registries);

        // set ID
        final BlockState state = model.getBlock().getDefaultState();
        super.setID(state);
        registries.registerBlockModel(this);

        System.out.println("Loaded model for Block with ID '" + state.getBlockID() + "', for state with ID '" + registries.blockState().getID(state) + "'");
    }

    @Override
    public void dispose() { }

}