package generaloss.mc24.client.resource;

import generaloss.mc24.client.level.renderer.block.BlockFace;
import generaloss.mc24.client.level.renderer.block.BlockModel;
import generaloss.mc24.client.level.renderer.block.BlockVertex;
import jpize.util.res.Resource;
import org.json.JSONArray;
import org.json.JSONObject;

public class ResourceBlockModel extends ResourceHandle<BlockModel> {

    private final BlockModel model;

    public ResourceBlockModel(ResourcesRegistry dispatcher, String identifier, String path) {
        super(dispatcher, identifier, path);
        this.model = new BlockModel();
    }

    @Override
    public BlockModel resource() {
        return model;
    }

    @Override
    public void reload() {
        Resource resource = Resource.external(super.dispatcher().getDirectory() + super.getPath());
        if(!resource.exists())
            resource = Resource.external(super.dispatcher().getDefaultDirectory() + super.getPath());

        final JSONObject jsonObject = new JSONObject(resource.readString());

        model.clear();
        model.setDontHideSameBlockFaces(jsonObject.getBoolean("dont_hide_same_block_faces"));

        final JSONObject jsonFaces = jsonObject.getJSONObject("faces");
        for(String faceKey: jsonFaces.keySet()){
            final JSONObject jsonFace = jsonFaces.getJSONObject(faceKey);
            // texture
            final String textureID = jsonFace.getString("texture_ID");
            // vertices
            final JSONArray vertices = jsonFace.getJSONArray("vertices");
            final BlockVertex[] verticesArray = new BlockVertex[vertices.length()];
            for(int i = 0; i < vertices.length(); i++){
                final JSONArray jsonVertex = vertices.getJSONArray(i);
                final float[] vertexArray = new float[BlockVertex.SIZE];
                for(int j = 0; j < jsonVertex.length(); j++)
                    vertexArray[j] = jsonVertex.getFloat(j);

                verticesArray[i] = new BlockVertex(
                    vertexArray[0], vertexArray[1], vertexArray[2], // position
                    vertexArray[3], vertexArray[4], // texcoord
                    vertexArray[5], vertexArray[6], vertexArray[7], vertexArray[8] // color
                );
            }

            model.addFace(new BlockFace(textureID, verticesArray));
        }
    }

    @Override
    public void dispose() { }

    // static {
    //     registerModel("grass_block", new BlockModel(false)
    //         // east
    //         .addFace(new BlockFace("grass_block_side", new BlockVertex[]{
    //             new BlockVertex(1F, 1, 0, 0, 0, 1, 1, 1, 1F),
    //             new BlockVertex(1F, 0, 0, 0, 1, 1, 1, 1, 1F),
    //             new BlockVertex(1F, 0, 1, 1, 1, 1, 1, 1, 1F),
    //             new BlockVertex(1F, 1, 1, 1, 0, 1, 1, 1, 1F)
    //         }))
    //         // west
    //         .addFace(new BlockFace("grass_block_side", new BlockVertex[]{
    //             new BlockVertex(0F, 1, 1, 0, 0, 1, 1, 1, 1F),
    //             new BlockVertex(0F, 0, 1, 0, 1, 1, 1, 1, 1F),
    //             new BlockVertex(0F, 0, 0, 1, 1, 1, 1, 1, 1F),
    //             new BlockVertex(0F, 1, 0, 1, 0, 1, 1, 1, 1F)
    //         }))
    //         // east overlay
    //         .addFace(new BlockFace("grass_block_side_overlay", new BlockVertex[]{
    //             new BlockVertex(1F, 1, 0, 0, 0, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(1F, 0, 0, 0, 1, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(1F, 0, 1, 1, 1, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(1F, 1, 1, 1, 0, 0.55F, 0.75F, 0.3F, 1F)
    //         }))
    //         // west overlay
    //         .addFace(new BlockFace("grass_block_side_overlay", new BlockVertex[]{
    //             new BlockVertex(0F, 1, 1, 0, 0, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(0F, 0, 1, 0, 1, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(0F, 0, 0, 1, 1, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(0F, 1, 0, 1, 0, 0.55F, 0.75F, 0.3F, 1F)
    //         }))
    //         // up
    //         .addFace(new BlockFace("grass_block_top", new BlockVertex[]{
    //             new BlockVertex(0, 1F, 1, 0, 0, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(0, 1F, 0, 0, 1, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(1, 1F, 0, 1, 1, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(1, 1F, 1, 1, 0, 0.55F, 0.75F, 0.3F, 1F)
    //         }))
    //         // down
    //         .addFace(new BlockFace("dirt", new BlockVertex[]{
    //             new BlockVertex(0, 0F, 0, 0, 0, 1, 1, 1, 1F),
    //             new BlockVertex(0, 0F, 1, 0, 1, 1, 1, 1, 1F),
    //             new BlockVertex(1, 0F, 1, 1, 1, 1, 1, 1, 1F),
    //             new BlockVertex(1, 0F, 0, 1, 0, 1, 1, 1, 1F)
    //         }))
    //         // north
    //         .addFace(new BlockFace("grass_block_side", new BlockVertex[]{
    //             new BlockVertex(1, 1, 1F, 0, 0, 1, 1, 1, 1F),
    //             new BlockVertex(1, 0, 1F, 0, 1, 1, 1, 1, 1F),
    //             new BlockVertex(0, 0, 1F, 1, 1, 1, 1, 1, 1F),
    //             new BlockVertex(0, 1, 1F, 1, 0, 1, 1, 1, 1F)
    //         }))
    //         // south
    //         .addFace(new BlockFace("grass_block_side", new BlockVertex[]{
    //             new BlockVertex(0, 1, 0F, 0, 0, 1, 1, 1, 1F),
    //             new BlockVertex(0, 0, 0F, 0, 1, 1, 1, 1, 1F),
    //             new BlockVertex(1, 0, 0F, 1, 1, 1, 1, 1, 1F),
    //             new BlockVertex(1, 1, 0F, 1, 0, 1, 1, 1, 1F)
    //         }))
    //         // north overlay
    //         .addFace(new BlockFace("grass_block_side_overlay", new BlockVertex[]{
    //             new BlockVertex(1, 1, 1F, 0, 0, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(1, 0, 1F, 0, 1, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(0, 0, 1F, 1, 1, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(0, 1, 1F, 1, 0, 0.55F, 0.75F, 0.3F, 1F)
    //         }))
    //         // south overlay
    //         .addFace(new BlockFace("grass_block_side_overlay", new BlockVertex[]{
    //             new BlockVertex(0, 1, 0F, 0, 0, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(0, 0, 0F, 0, 1, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(1, 0, 0F, 1, 1, 0.55F, 0.75F, 0.3F, 1F),
    //             new BlockVertex(1, 1, 0F, 1, 0, 0.55F, 0.75F, 0.3F, 1F)
    //         })));

    //     registerModel("oak_leaves", new BlockModel(true)
    //         // east
    //         .addFace(new BlockFace(Directory.NONE, "oak_leaves", new BlockVertex[]{
    //             new BlockVertex(1F, 1, 0, 0, 0, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(1F, 0, 0, 0, 1, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(1F, 0, 1, 1, 1, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(1F, 1, 1, 1, 0, 0.4F, 0.7F, 0.3F, 1F),
    //         }))
    //         // west
    //         .addFace(new BlockFace(Directory.NONE, "oak_leaves", new BlockVertex[]{
    //             new BlockVertex(0F, 1, 1, 0, 0, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(0F, 0, 1, 0, 1, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(0F, 0, 0, 1, 1, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(0F, 1, 0, 1, 0, 0.4F, 0.7F, 0.3F, 1F),
    //         }))
    //         // up
    //         .addFace(new BlockFace(Directory.NONE, "oak_leaves", new BlockVertex[]{
    //             new BlockVertex(0, 1F, 1, 0, 0, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(0, 1F, 0, 0, 1, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(1, 1F, 0, 1, 1, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(1, 1F, 1, 1, 0, 0.4F, 0.7F, 0.3F, 1F),
    //         }))
    //         // down
    //         .addFace(new BlockFace(Directory.NONE, "oak_leaves", new BlockVertex[]{
    //             new BlockVertex(0, 0F, 0, 0, 0, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(0, 0F, 1, 0, 1, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(1, 0F, 1, 1, 1, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(1, 0F, 0, 1, 0, 0.4F, 0.7F, 0.3F, 1F),
    //         }))
    //         // north
    //         .addFace(new BlockFace(Directory.NONE, "oak_leaves", new BlockVertex[]{
    //             new BlockVertex(1, 1, 1F, 0, 0, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(1, 0, 1F, 0, 1, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(0, 0, 1F, 1, 1, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(0, 1, 1F, 1, 0, 0.4F, 0.7F, 0.3F, 1F),
    //         }))
    //         // south
    //         .addFace(new BlockFace(Directory.NONE, "oak_leaves", new BlockVertex[]{
    //             new BlockVertex(0, 1, 0F, 0, 0, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(0, 0, 0F, 0, 1, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(1, 0, 0F, 1, 1, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(1, 1, 0F, 1, 0, 0.4F, 0.7F, 0.3F, 1F),
    //         })).addFace(new BlockFace(Directory.NONE, Directory.NONE, "oak_leaves_bushy_2", new BlockVertex[]{
    //             new BlockVertex(-0.4F, -0.4F, 1.4F, 0, 0, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(-0.4F, -0.4F, -0.4F, 0, 1, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(1.4F, 1.4F, -0.4F, 1, 1, 0.4F, 0.7F, 0.3F, 1F),
    //             new BlockVertex(1.4F, 1.4F, 1.4F, 1, 0, 0.4F, 0.7F, 0.3F, 1F),
    //         })));

    //     registerModel("stone", new BlockModel(false)
    //         // east
    //         .addFace(new BlockFace("stone", new BlockVertex[]{
    //             new BlockVertex(1F, 1, 0, 0, 0, 1, 1, 1, 1F), new BlockVertex(1F, 0, 0, 0, 1, 1, 1, 1, 1F),
    //             new BlockVertex(1F, 0, 1, 1, 1, 1, 1, 1, 1F), new BlockVertex(1F, 1, 1, 1, 0, 1, 1, 1, 1F)
    //         }))
    //         // west
    //         .addFace(new BlockFace("stone", new BlockVertex[]{
    //             new BlockVertex(0F, 1, 1, 0, 0, 1, 1, 1, 1F), new BlockVertex(0F, 0, 1, 0, 1, 1, 1, 1, 1F),
    //             new BlockVertex(0F, 0, 0, 1, 1, 1, 1, 1, 1F), new BlockVertex(0F, 1, 0, 1, 0, 1, 1, 1, 1F)
    //         }))
    //         // up
    //         .addFace(new BlockFace("stone", new BlockVertex[]{
    //             new BlockVertex(0, 1F, 1, 0, 0, 1, 1, 1, 1F), new BlockVertex(0, 1F, 0, 0, 1, 1, 1, 1, 1F),
    //             new BlockVertex(1, 1F, 0, 1, 1, 1, 1, 1, 1F), new BlockVertex(1, 1F, 1, 1, 0, 1, 1, 1, 1F)
    //         }))
    //         // down
    //         .addFace(new BlockFace("stone", new BlockVertex[]{
    //             new BlockVertex(0, 0F, 0, 0, 0, 1, 1, 1, 1F), new BlockVertex(0, 0F, 1, 0, 1, 1, 1, 1, 1F),
    //             new BlockVertex(1, 0F, 1, 1, 1, 1, 1, 1, 1F), new BlockVertex(1, 0F, 0, 1, 0, 1, 1, 1, 1F)
    //         }))
    //         // north
    //         .addFace(new BlockFace("stone", new BlockVertex[]{
    //             new BlockVertex(1, 1, 1F, 0, 0, 1, 1, 1, 1F), new BlockVertex(1, 0, 1F, 0, 1, 1, 1, 1, 1F),
    //             new BlockVertex(0, 0, 1F, 1, 1, 1, 1, 1, 1F), new BlockVertex(0, 1, 1F, 1, 0, 1, 1, 1, 1F)
    //         }))
    //         // south
    //         .addFace(new BlockFace("stone", new BlockVertex[]{
    //             new BlockVertex(0, 1, 0F, 0, 0, 1, 1, 1, 1F), new BlockVertex(0, 0, 0F, 0, 1, 1, 1, 1, 1F),
    //             new BlockVertex(1, 0, 0F, 1, 1, 1, 1, 1, 1F), new BlockVertex(1, 1, 0F, 1, 0, 1, 1, 1, 1F)
    //         })));
    // }

}
