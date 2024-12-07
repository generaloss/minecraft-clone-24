package generaloss.mc24.server.registry;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.resourcepack.ResourceBlock;

public class RegistryBlocks extends ResourceRegistry<String, ResourceBlock> {

    public Block get(String ID) {
        return super.getResource(ID).getObject();
    }

    public ResourceBlock register(String path) {
        return super.registerResource(new ResourceBlock(path));
    }

    public ResourceBlock register(ResourceBlock blockResource) {
        return super.registerResource(blockResource);
    }

}
