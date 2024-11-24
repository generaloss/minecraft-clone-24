package generaloss.mc24.client.resource;

import generaloss.mc24.server.block.Block;
import jpize.util.res.Resource;
import org.json.JSONObject;

public class ResourceBlock extends ResourceHandle<Block> {

    private final Block block;

    public ResourceBlock(ResourcesRegistry dispatcher, String identifier, String path) {
        super(dispatcher, identifier, path);
        this.block = new Block();
    }

    @Override
    public Block resource() {
        return block;
    }

    @Override
    public void reload() {
        Resource resource = Resource.external(super.dispatcher().getDirectory() + super.getPath());
        if(!resource.exists())
            resource = Resource.external(super.dispatcher().getDefaultDirectory() + super.getPath());

        final JSONObject object = new JSONObject(resource.readString());
        final String stringID = object.getString("string_ID");
        final int numericID = object.getInt("numeric_ID");

        block.setStringID(stringID);
        block.setNumericID(numericID);
    }

    @Override
    public void dispose() { }

}
