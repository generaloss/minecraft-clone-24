package generaloss.mc24.server.resourcepack;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.registry.Registries;
import jpize.util.res.Resource;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

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

        // read json
        final JSONObject jsonObject = new JSONObject(resource.readString());
        final String blockID = jsonObject.getString("block_ID");

        // load
        block.setID(blockID);
        block.buildStates(Map.of(), registries);

        System.out.println("Loaded block '" + blockID + "'");
    }

    @Override
    public void reload(Collection<ResourcePack> packs) { }

    @Override
    public void dispose() { }

}