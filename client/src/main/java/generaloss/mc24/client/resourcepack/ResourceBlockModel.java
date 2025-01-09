package generaloss.mc24.client.resourcepack;

import generaloss.mc24.client.block.BlockModel;
import generaloss.mc24.client.block.BlockModelLoader;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.resourcepack.ResourceHandle;
import generaloss.mc24.server.resourcepack.ResourcePack;
import generaloss.mc24.server.resourcepack.ResourcePackManager;
import jpize.util.res.Resource;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class ResourceBlockModel extends ResourceHandle<BlockState, BlockModel> {

    private final BlockState blockstate;
    private final ResourcePackManager resourcePackManager;
    private final List<BlockModel> models;

    public ResourceBlockModel(String path, BlockState blockstate, ResourcePackManager resourcePackManager) {
        super(path);
        this.blockstate = blockstate;
        this.resourcePackManager = resourcePackManager;
        this.models = new ArrayList<>();
    }

    @Override
    public BlockState getID() {
        return blockstate;
    }

    @Override
    public BlockModel getObject() {
        return models.get(0);
    }

    @Override
    public void load(ResourcePack pack) {
        final Resource resource = pack.getResource(super.getPath());
        this.loadVariantsJSON(resource);
        // System.out.println("Loaded model of block '" + model.getBlockState().getBlockID() + "' for state '" + registries.BLOCK_STATES.getID(model.getBlockState()) + "'");
    }

    @Override
    public void reload(Collection<ResourcePack> packs) {
        models.clear();
        final Resource resource = super.getResourceFromPacks(packs, super.getPath());
        this.loadVariantsJSON(resource);
        // System.out.println("Reloaded model of block '" + model.getBlockState().getBlockID() + "' for state '" + registries.BLOCK_STATES.getID(model.getBlockState()) + "'");
    }

    @Override
    public void dispose() { }


    private static String prevBlockID = null;

    private void loadVariantsJSON(Resource resource) {
        if(!blockstate.getBlockID().equals(prevBlockID))
            System.out.println("Load '" + blockstate.getBlockID() + "' models:");
        prevBlockID = blockstate.getBlockID();

        final JSONObject jsonObject = new JSONObject(resource.readString());
        final JSONObject jsonVariants = jsonObject.getJSONObject("variants");

        final StringJoiner variantKeyJoiner = new StringJoiner(",");
        blockstate.getStateProperties().forEach((property, value) -> {
            variantKeyJoiner.add(property.getName() + "=" + value);
        });
        final String variantKey = variantKeyJoiner.toString();

        if(!jsonVariants.has(variantKey))
            System.err.println("Not found model for block '" + blockstate.getBlockID() + "' variant: " + variantKeyJoiner);

        System.out.println("  * model variant '" + variantKeyJoiner + "'");
        final Object jsonVariantObject = jsonVariants.get(variantKey);

        if(jsonVariantObject instanceof JSONObject jsonVariant){
            final BlockModel model = BlockModelLoader.loadVariantJSON(blockstate, jsonVariant, resourcePackManager);
            models.add(model);

        }else if(jsonVariantObject instanceof JSONArray jsonArrayVariants){
            for(int i = 0; i < jsonArrayVariants.length(); i++){

                final JSONObject jsonVariant = jsonArrayVariants.getJSONObject(i);
                final BlockModel model = BlockModelLoader.loadVariantJSON(blockstate, jsonVariant, resourcePackManager);
                models.add(model);
            }
        }
    }

}