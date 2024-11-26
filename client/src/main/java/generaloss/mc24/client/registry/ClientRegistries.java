package generaloss.mc24.client.registry;

import generaloss.mc24.client.block.BlockStateModel;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.registry.Registry;
import jpize.util.Disposable;

public class ClientRegistries extends Registries implements Disposable {

    private final Registry<BlockState, BlockStateModel> blockModel;
    private final ResourceRegistry resource;

    public ClientRegistries() {
        this.blockModel = new Registry<>();
        this.resource = new ResourceRegistry();
    }

    public Registry<BlockState, BlockStateModel> blockModel() {
        return blockModel;
    }

    public ResourceRegistry resource() {
        return resource;
    }

    @Override
    public void dispose() {
        resource.dispose();
    }

}
