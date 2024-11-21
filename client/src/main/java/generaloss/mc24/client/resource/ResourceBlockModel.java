package generaloss.mc24.client.resource;

import generaloss.mc24.client.level.renderer.block.BlockModel;
import jpize.util.res.Resource;

public class ResourceBlockModel extends ResourceHandle<BlockModel> {

    private final BlockModel blockModel;

    public ResourceBlockModel(ResourceDispatcher dispatcher, String identifier, String path) {
        super(dispatcher, identifier, path);
        this.blockModel = new BlockModel();
    }

    @Override
    public BlockModel resource() {
        return blockModel;
    }

    @Override
    public void reload() {
        Resource resource = Resource.external(super.dispatcher().getRootDirectory() + super.getPath());
        if(!resource.exists())
            resource = Resource.external(ResourceDispatcher.DEFAULT_ROOT_DIR + super.getPath());

        blockModel.load(resource);
    }

    @Override
    public void dispose() { }

}
