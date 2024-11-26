package generaloss.mc24.client.block;

import generaloss.mc24.server.Directory;
import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.registry.Registries;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockStateModel {

    private Block block;
    private boolean dontHidesSameBlockFaces;
    private final Map<Directory, List<BlockFace>> faceGroups;
    private final Map<Directory, Boolean> hideOppositeFacesMap;

    public BlockStateModel() {
        this.faceGroups = new HashMap<>();
        this.hideOppositeFacesMap = new HashMap<>();
    }


    public Block getBlock() {
        return block;
    }

    public BlockStateModel setBlock(Block block) {
        this.block = block;
        return this;
    }

    public boolean isDontHidesSameBlockFaces() {
        return dontHidesSameBlockFaces;
    }

    public BlockStateModel setDontHidesSameBlockFaces(boolean dontHideSameBlockFaces) {
        this.dontHidesSameBlockFaces = dontHideSameBlockFaces;
        return this;
    }


    public List<BlockFace> getFacesGroup(Directory dir) {
        return faceGroups.getOrDefault(dir, List.of());
    }

    public BlockStateModel addFace(BlockFace face) {
        // add face
        final Directory dir = face.getHidesFace().opposite();
        final List<BlockFace> group = faceGroups.getOrDefault(dir, new ArrayList<>());
        group.add(face);
        faceGroups.put(dir, group);
        // hide faces map
        final Directory hideOppositeFace = face.getHideOppositeFace();
        if(hideOppositeFace != Directory.NONE)
            hideOppositeFacesMap.put(hideOppositeFace, true);
        return this;
    }


    public boolean isHidesOppositeFace(Directory dir) {
        return hideOppositeFacesMap.containsKey(dir);
    }

    public boolean isNotHidesOppositeFace(Directory dir) {
        return !this.isHidesOppositeFace(dir);
    }


    public BlockStateModel loadFromJSON(String jsonString, Registries registries) {
        final JSONObject jsonObject = new JSONObject(jsonString);

        // set block
        final String blockID = jsonObject.getString("block_ID");
        final Block block = registries.block().get(blockID);
        if(block == null)
            throw new IllegalStateException("Block model cannot be loaded. Block with ID '" + blockID + "' is not exists.");
        this.setBlock(block);

        // set 'dont hides same block faces'
        this.setDontHidesSameBlockFaces(jsonObject.getBoolean("dont_hides_same_block_faces"));

        // clear
        faceGroups.clear();
        hideOppositeFacesMap.clear();

        // load faces
        final JSONArray jsonFaces = jsonObject.getJSONArray("faces");
        for(int i = 0; i < jsonFaces.length(); i++) {
            final JSONObject jsonFace = jsonFaces.getJSONObject(i);
            final BlockFace face = new BlockFace();

            // set 'texture ID'
            final String textureID = jsonFace.getString("texture_ID");
            face.setTextureID(textureID);

            // set 'vertices'
            final JSONArray vertices = jsonFace.getJSONArray("vertices");
            final BlockVertex[] verticesArray = new BlockVertex[vertices.length()];
            for(int j = 0; j < vertices.length(); j++){
                final JSONArray jsonVertex = vertices.getJSONArray(j);
                final float[] vertexArray = new float[BlockVertex.SIZE];
                for(int k = 0; k < jsonVertex.length(); k++)
                    vertexArray[k] = jsonVertex.getFloat(k);

                verticesArray[j] = new BlockVertex(
                        vertexArray[0], vertexArray[1], vertexArray[2], // position
                        vertexArray[3], vertexArray[4], // texcoord
                        vertexArray[5], vertexArray[6], vertexArray[7], vertexArray[8] // color
                );
            }
            face.setVertices(verticesArray);

            // set 'hides face'
            if(jsonFace.has("hides_face")){
                face.setHidesFace(Directory.valueOf(jsonFace.getString("hides_face")));
            }else{
                face.calculateHidesFace();
            }

            // set 'hide oposite face'
            if(jsonFace.has("hide_opposite_face")){
                face.setHideOppositeFace(Directory.valueOf(jsonFace.getString("hide_opposite_face")));
            }else{
                face.calculateHideOppositeFace();
            }

            this.addFace(face);
        }

        return this;
    }

}
