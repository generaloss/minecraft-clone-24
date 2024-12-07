package generaloss.mc24.server.resourcepack;

import generaloss.mc24.server.block.Block;
import jpize.util.res.Resource;
import java.util.Collection;

public class ResourceBlock extends ResourceHandle<String, Block> {

    private final Block block;

    public ResourceBlock(String path) {
        super(path);
        this.block = new Block();
    }

    public ResourceBlock(Block block) {
        super(null);
        this.block = block;
        // System.out.println("Loaded block '" + block.getID() + "'");
    }

    @Override
    public String getID() {
        return block.getID();
    }

    @Override
    public Block getObject() {
        return block;
    }

    @Override
    public void load(ResourcePack defaultPack) {
        final String path = super.getPath();
        if(path == null)
            return;

        final Resource resource = defaultPack.getResource(path);
        block.loadFromJSON(resource.readString());
        // System.out.println("Loaded block '" + block.getID() + "'");
    }

    @Override
    public void reload(Collection<ResourcePack> packs) { }

    @Override
    public void dispose() { }

}