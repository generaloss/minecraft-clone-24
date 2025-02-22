package generaloss.mc24.client.resource;

import generaloss.mc24.client.block.BlockModel;
import generaloss.mc24.client.block.BlockModelLoader;
import generaloss.mc24.server.block.BlockState;
import jpize.util.res.Resource;
import jpize.util.res.ResourceSource;
import jpize.util.res.handle.ResHandle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class BlockModelHandle extends ResHandle<BlockState, BlockModel> {

    private final BlockState blockstate;
    private final List<BlockModel> models;

    public BlockModelHandle(BlockState key, String path) {
        super(key, path);
        this.blockstate = key;
        this.models = new ArrayList<>();
    }

    @Override
    public BlockModel resource() {
        return models.get(0);
    }

    @Override
    public void load(ResourceSource source, String path) {
        final Resource resource = source.getResource(path);
        this.loadVariantsJSON(source, resource);
        // System.out.println("Loaded model of block '" + model.getBlockState().getBlockID() + "' for state '" + registries.BLOCK_STATES.getID(model.getBlockState()) + "'");
    }

    @Override
    public void dispose() { }


    private static String prevBlockID = null;

    private void loadVariantsJSON(ResourceSource resSource, Resource resource) {
        if(!blockstate.getBlockID().equals(prevBlockID))
            System.out.println("Load '" + blockstate.getBlockID() + "' models:");
        prevBlockID = blockstate.getBlockID();

        final JSONObject jsonObject = new JSONObject(resource.readString());
        final JSONObject jsonVariants = jsonObject.getJSONObject("variants");

        final StringJoiner variantKeyJoiner = new StringJoiner(",");
        blockstate.getStateProperties().forEach((entry) ->
            variantKeyJoiner.add(entry.getKey().getName() + "=" + entry.getValue())
        );
        final String variantKey = variantKeyJoiner.toString();

        if(!jsonVariants.has(variantKey))
            System.err.println("Not found model for block '" + blockstate.getBlockID() + "' variant: " + variantKeyJoiner);

        System.out.println("  * model variant '" + variantKeyJoiner + "'");
        final Object jsonVariantObject = jsonVariants.get(variantKey);

        if(jsonVariantObject instanceof JSONObject jsonVariant){
            final BlockModel model = BlockModelLoader.loadVariantJSON(blockstate, jsonVariant, resSource);
            models.add(model);

        }else if(jsonVariantObject instanceof JSONArray jsonArrayVariants){
            for(int i = 0; i < jsonArrayVariants.length(); i++){

                final JSONObject jsonVariant = jsonArrayVariants.getJSONObject(i);
                final BlockModel model = BlockModelLoader.loadVariantJSON(blockstate, jsonVariant, resSource);
                models.add(model);
            }
        }
    }

}