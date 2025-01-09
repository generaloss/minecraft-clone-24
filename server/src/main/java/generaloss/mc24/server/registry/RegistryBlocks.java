package generaloss.mc24.server.registry;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.resourcepack.ResourceBlock;

public class RegistryBlocks extends ResourceRegistry<String, ResourceBlock> {

    public Block get(String ID) {
        final ResourceBlock block = super.getResource(ID);
        if(block == null)
            throw new IllegalArgumentException("Unknown block ID: " + ID);
        return block.getObject();
    }

    public ResourceBlock register(String path) {
        return super.registerResource(new ResourceBlock(path));
    }

    public ResourceBlock register(ResourceBlock blockResource) {
        return super.registerResource(blockResource);
    }

}
