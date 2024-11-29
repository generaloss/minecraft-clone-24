package generaloss.mc24.server.resourcepack;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.registry.Registries;
import jpize.util.res.Resource;
import java.util.Collection;

public class ResourceBlock extends ResourceHandle<String, Block> {

    private final Registries registries;
    private final Block block;

    public ResourceBlock(String path, Registries registries) {
        super(path);
        this.registries = registries;
        this.block = new Block();
    }

    public ResourceBlock(Block block) {
        super(null);
        this.registries = null;
        this.block = block;
        System.out.println("Loaded block '" + block.getID() + "'");
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
        if(registries == null)
            return;

        final Resource resource = defaultPack.getResource(super.getPath());
        block.loadFromJSON(resource.readString(), registries);
        System.out.println("Loaded block '" + block.getID() + "'");
    }

    @Override
    public void reload(Collection<ResourcePack> packs) { }

    @Override
    public void dispose() { }

}