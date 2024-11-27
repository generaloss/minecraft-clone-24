package generaloss.mc24.server.resourcepack;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.Registries;
import jpize.util.res.Resource;
import org.json.JSONObject;

import java.util.Map;

public class ResourceBlock extends ResourceHandle<String, Block> {

    private final Registries registries;
    private final Block block;

    public ResourceBlock(ResourcePack defaultPack, Registries registries, String path) {
        super(defaultPack, path);
        this.registries = registries;
        this.block = new Block();
    }

    @Override
    public Block object() {
        return block;
    }

    @Override
    public void load(ResourcePack pack) {
        final Resource resource = pack.getOrDefault(super.getPath(), super.getDefaultPack());

        // read json
        final JSONObject jsonObject = new JSONObject(resource.readString());
        final String blockID = jsonObject.getString("block_ID");
        // create block
        final Block block = new Block(blockID, Map.of(), registries.blockState());

        registries.block().register(block);

        System.out.println("Loaded Block with ID '" + blockID + "'");
    }

    @Override
    public void dispose() { }

}