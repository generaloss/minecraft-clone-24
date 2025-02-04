package generaloss.mc24.server.resourcepack;

import generaloss.mc24.server.block.Block;
import jpize.util.res.Resource;
import jpize.util.res.ResourceSource;
import jpize.util.res.handle.ResHandle;

public class BlockHandle extends ResHandle<String, Block> {

    private final Block block;
    private boolean initialized;

    public BlockHandle(String key, String path) {
        super(key, path);
        this.block = new Block();
    }

    public BlockHandle(Block block) {
        super(block.getID(), null);
        this.block = block;
    }

    @Override
    public Block resource() {
        return block;
    }

    @Override
    public void load(ResourceSource source, String path) {
        if(initialized || path == null)
            return;
        initialized = true;

        final Resource resource = source.getResource(path);
        block.loadFromJSON(resource);
    }

    @Override
    public void dispose() { }

}