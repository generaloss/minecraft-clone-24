package generaloss.mc24.client.meshing.block;

import generaloss.mc24.server.common.Direction;
import generaloss.mc24.server.block.BlockState;
import jpize.util.math.matrix.Matrix4f;
import jpize.util.math.vector.Vec3f;
import jpize.util.region.Region;
import jpize.util.res.Resource;
import jpize.util.res.ResourceSource;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BlockModelLoader {

    public static BlockModel loadVariantJSON(BlockState blockstate, JSONObject json, ResourceSource resSource) {
        final String modelPath = json.getString("model");

        // global rotation
        final Matrix4f blockRotationMat = new Matrix4f();
        if(json.has("x")) blockRotationMat.rotateX(json.getFloat("x"));
        if(json.has("y")) blockRotationMat.rotateY(json.getFloat("y"));
        if(json.has("z")) blockRotationMat.rotateZ(json.getFloat("z"));

        // uvlock
        final boolean uvLock = (json.has("uvlock") && json.getBoolean("uvlock"));

        // load
        final BlockModel model = new BlockModel(blockstate);
        loadModelJSON(model, blockstate, modelPath, blockRotationMat, uvLock, resSource, new HashMap<>());
        return model;
    }

    private static void loadModelJSON(BlockModel model, BlockState blockstate, String modelPath,
                                      Matrix4f blockRotationMat, boolean uvLock,
                                      ResourceSource resSource, Map<String, String> textureMap) {

        final Resource modelResource = resSource.getResource(modelPath);
        if(modelResource == null)
            throw new IllegalStateException("Model not found: '" + modelPath + "'");

        final JSONObject jsonModel = new JSONObject(modelResource.readString());

        // map textures
        if(jsonModel.has("textures")) {
            final JSONObject textures = jsonModel.getJSONObject("textures");
            for(String key: textures.keySet()){
                final String texturePath = textures.getString(key);
                textureMap.put("#" + key, texturePath);
            }
        }

        // ambient occlusion
        if(jsonModel.has("ambientocclusion")) {
            final boolean ambientOcclusion = jsonModel.getBoolean("ambientocclusion");
            model.setHasAmbientOcclusion(ambientOcclusion);
        }

        // recursive load parent
        if(jsonModel.has("parent")) {
            final String parentPath = jsonModel.getString("parent");
            loadModelJSON(model, blockstate, parentPath, blockRotationMat, uvLock, resSource, textureMap);
        }

        // normalize texture map
        textureMap.forEach((key, value) -> {
            if(value.startsWith("#"))
                textureMap.put(key, textureMap.get(value));
        });

        // load model elements
        if(jsonModel.has("elements")) {
            final JSONArray jsonElements = jsonModel.getJSONArray("elements");
            for(int i = 0; i < jsonElements.length(); i++){
                final JSONObject jsonElement = jsonElements.getJSONObject(i);

                // from
                final JSONArray jsonFrom = jsonElement.getJSONArray("from");
                final Vec3f from = new Vec3f(jsonFrom.getFloat(0), jsonFrom.getFloat(1), jsonFrom.getFloat(2));
                from.div(16F);

                // to
                final JSONArray jsonTo = jsonElement.getJSONArray("to");
                final Vec3f to = new Vec3f(jsonTo.getFloat(0), jsonTo.getFloat(1), jsonTo.getFloat(2));
                to.div(16F);

                // shade
                final boolean shade = (!jsonElement.has("shade") || jsonElement.getBoolean("shade"));

                // rotation
                final Matrix4f rotationMat = new Matrix4f();
                final Vec3f rotationOrigin = new Vec3f();
                if(jsonElement.has("rotation")) {
                    final JSONObject jsonRotation = jsonElement.getJSONObject("rotation");
                    final JSONArray jsonOrigin = jsonRotation.getJSONArray("origin");
                    rotationOrigin.set(jsonOrigin.getFloat(0), jsonOrigin.getFloat(1), jsonOrigin.getFloat(2));
                    rotationOrigin.div(16F);
                    final String axis = jsonRotation.getString("axis");
                    final float angle = -jsonRotation.getFloat("angle");
                    switch(axis){
                        case "x" -> rotationMat.setRotationX(angle);
                        case "y" -> rotationMat.setRotationY(angle);
                        case "z" -> rotationMat.setRotationZ(angle);
                    }
                }

                // faces
                final JSONObject jsonFaces = jsonElement.getJSONObject("faces");
                for(String faceKey: jsonFaces.keySet()) {
                    final JSONObject jsonFace = jsonFaces.getJSONObject(faceKey);
                    final Direction direction = Direction.valueOf(faceKey.toUpperCase());
                    final BlockFace face = loadModelFace(direction, from, to, shade,
                        rotationOrigin, rotationMat, blockRotationMat, uvLock, jsonFace, textureMap);
                    model.addFace(face);
                }
            }
        }
    }

    private static BlockFace loadModelFace(Direction direction, Vec3f from, Vec3f to, boolean shade,
                                           Vec3f rotationOrigin, Matrix4f rotationMat, Matrix4f blockRotationMat,
                                           boolean uvLock, JSONObject jsonFace, Map<String, String> textureMap) {

        if(direction.isNone())
            throw new IllegalStateException("Illegal 'none' face direction");

        final BlockFace face = new BlockFace();

        // set texture ID
        final String textureID = textureMap.get(jsonFace.getString("texture"));
        face.setTextureID(textureID);

        // set cullface
        final Direction cullface;
        if(jsonFace.has("cullface")){
            final Direction cullfaceDir = Direction.valueOf(jsonFace.getString("cullface").toUpperCase());
            cullface = cullfaceDir.getRotated(blockRotationMat);
        }else{
            cullface = Direction.NONE;
        }
        face.setCulling(cullface);

        // region
        final Region region = new Region();
        if(jsonFace.has("uv")) {
            final JSONArray jsonUV = jsonFace.getJSONArray("uv");
            region.set(
                jsonUV.getFloat(0) / 16F, jsonUV.getFloat(1) / 16F,
                jsonUV.getFloat(2) / 16F, jsonUV.getFloat(3) / 16F
            );
        }

        // tint index
        final int tintIndex;
        if(jsonFace.has("tintindex")) {
            tintIndex = jsonFace.getInt("tintindex");
        }else{
            tintIndex = -1;
        }
        face.setTintIndex(tintIndex);

        // set vertices
        final BlockVertex v1 = new BlockVertex(0, 0, 0, region.u1, region.v1, 1F, 1F, 1F, 1F);
        final BlockVertex v2 = new BlockVertex(0, 0, 0, region.u1, region.v2, 1F, 1F, 1F, 1F);
        final BlockVertex v3 = new BlockVertex(0, 0, 0, region.u2, region.v2, 1F, 1F, 1F, 1F);
        final BlockVertex v4 = new BlockVertex(0, 0, 0, region.u2, region.v1, 1F, 1F, 1F, 1F);

        switch(direction) {
            case WEST -> {
                v1.setPosition(from.x, to.y  , to.z  );
                v2.setPosition(from.x, from.y, to.z  );
                v3.setPosition(from.x, from.y, from.z);
                v4.setPosition(from.x, to.y  , from.z);
            }
            case EAST -> {
                v1.setPosition(to.x  , to.y  , from.z);
                v2.setPosition(to.x  , from.y, from.z);
                v3.setPosition(to.x  , from.y, to.z  );
                v4.setPosition(to.x  , to.y  , to.z  );
            }
            case DOWN -> {
                v1.setPosition(from.x, from.y, from.z);
                v2.setPosition(from.x, from.y, to.z  );
                v3.setPosition(to.x  , from.y, to.z  );
                v4.setPosition(to.x  , from.y, from.z);
            }
            case UP -> {
                v1.setPosition(from.x, to.y  , to.z  );
                v2.setPosition(from.x, to.y  , from.z);
                v3.setPosition(to.x  , to.y  , from.z);
                v4.setPosition(to.x  , to.y  , to.z  );
            }
            case SOUTH -> {
                v1.setPosition(from.x, to.y  , from.z);
                v2.setPosition(from.x, from.y, from.z);
                v3.setPosition(to.x  , from.y, from.z);
                v4.setPosition(to.x  , to.y  , from.z);
            }
            case NORTH -> {
                v1.setPosition(to.x  , to.y  , to.z  );
                v2.setPosition(to.x  , from.y, to.z  );
                v3.setPosition(from.x, from.y, to.z  );
                v4.setPosition(from.x, to.y  , to.z  );
            }
        }

        // shade
        direction = direction.getRotated(blockRotationMat);
        if(shade) {
            final float shading = direction.getBlockShade();
            v1.setColor(v1.getR() * shading, v1.getG() * shading, v1.getB() * shading);
            v2.setColor(v2.getR() * shading, v2.getG() * shading, v2.getB() * shading);
            v3.setColor(v3.getR() * shading, v3.getG() * shading, v3.getB() * shading);
            v4.setColor(v4.getR() * shading, v4.getG() * shading, v4.getB() * shading);
        }
        face.setDirection(direction);

        v1.rotate(rotationOrigin, rotationMat, blockRotationMat, uvLock);
        v2.rotate(rotationOrigin, rotationMat, blockRotationMat, uvLock);
        v3.rotate(rotationOrigin, rotationMat, blockRotationMat, uvLock);
        v4.rotate(rotationOrigin, rotationMat, blockRotationMat, uvLock);

        face.setVertices(v1, v2, v3, v4);
        return face;
    }

}
