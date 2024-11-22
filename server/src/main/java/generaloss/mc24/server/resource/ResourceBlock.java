package generaloss.mc24.server.resource;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockFace;
import generaloss.mc24.server.block.BlockModel;
import generaloss.mc24.server.block.BlockVertex;
import jpize.util.res.Resource;
import org.json.JSONArray;
import org.json.JSONObject;

public class ResourceBlock extends ResourceHandle<Block> {

    private final Block block;

    public ResourceBlock(ResourceDispatcher dispatcher, String identifier, String path) {
        super(dispatcher, identifier, path);
        this.block = new Block(identifier);
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

        if(object.has("model")) {
            block.setModel(new BlockModel());

            final JSONObject model = object.getJSONObject("model");
            block.model().setDontHideSameBlockFaces(model.getBoolean("dont_hide_same_block_faces"));

            final JSONObject faces = model.getJSONObject("faces");
            for(String faceKey: faces.keySet()){
                final JSONObject face = faces.getJSONObject(faceKey);
                // texture
                final String textureID = face.getString("texture_ID");
                // vertices
                final JSONArray vertices = face.getJSONArray("vertices");
                final BlockVertex[] verticesArray = new BlockVertex[vertices.length()];
                for(int i = 0; i < vertices.length(); i++){
                    final JSONArray vertex = vertices.getJSONArray(i);
                    final float[] vertexArray = new float[BlockVertex.SIZE];
                    for(int j = 0; j < vertex.length(); j++)
                        vertexArray[j] = vertex.getFloat(j);

                    verticesArray[i] = new BlockVertex(
                        vertexArray[0], vertexArray[1], vertexArray[2], // position
                        vertexArray[3], vertexArray[4], // texcoord
                        vertexArray[5], vertexArray[6], vertexArray[7], vertexArray[8] // color
                    );
                }

                block.model().addFace(new BlockFace(textureID, verticesArray));
            }
        }
    }

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

    @Override
    public void dispose() { }

}
