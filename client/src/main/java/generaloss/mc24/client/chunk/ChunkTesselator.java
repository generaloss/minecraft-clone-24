package generaloss.mc24.client.chunk;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.block.BlockFace;
import generaloss.mc24.client.block.BlockStateModel;
import generaloss.mc24.client.block.BlockVertex;
import generaloss.mc24.server.Directory;
import generaloss.mc24.server.block.*;
import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.client.resourcepack.ResourceAtlas;
import generaloss.mc24.server.world.BlockLightEngine;
import generaloss.mc24.server.world.ChunkCache;
import jpize.app.Jpize;
import jpize.util.Disposable;
import jpize.util.array.FloatList;
import jpize.util.math.Maths;
import jpize.util.region.TextureRegion;
import jpize.util.time.Stopwatch;

import java.util.LinkedList;
import java.util.Queue;

public class ChunkTesselator implements Disposable {

    private final Main context;
    private final Queue<LevelChunk> taskQueue;
    private final ChunkCache<LevelChunk> chunkCache;

    private final ChunkMeshCache meshCache;
    private final FloatList verticesCache;

    private final BlockState[][][] blockCache;
    private final int[][][] lightLevelCache;
    private final float[] aoCache;

    public ChunkTesselator(Main context, WorldLevel level) {
        this.context = context;
        this.taskQueue = new LinkedList<>();
        this.chunkCache = new ChunkCache<>(level);

        this.meshCache = new ChunkMeshCache();
        this.verticesCache = new FloatList();

        this.blockCache = new BlockState[3][3][3];
        this.lightLevelCache = new int[3][3][3];
        this.aoCache = new float[BlockFace.VERTICES_NUMBER];
    }

    public void tesselate(LevelChunk chunk) {
        if(chunk == null)
            return;
        taskQueue.add(chunk);
    }

    public void update() {
        final Stopwatch timer = new Stopwatch().start();
        while(!taskQueue.isEmpty()) {
            this.processTesselate(taskQueue.poll());

            if(timer.getMillis() > Jpize.getDeltaTime() * 500)
                break;
        }
    }

    private BlockState getCachedBlockState(int i, int j, int k) {
        return blockCache[i + 1][j + 1][k + 1];
    }

    private BlockState getCachedBlockState(Directory dir) {
        return this.getCachedBlockState(dir.getX(), dir.getY(), dir.getZ());
    }

    public int getCachedLightLevel(int i, int j, int k) {
        return lightLevelCache[i + 1][j + 1][k + 1];
    }

    private BlockStateModel getBlockModel(BlockState blockState) {
        return context.registries().getBlockModel(blockState);
    }


    private float evaluateAmbientOcclusion(BlockVertex vertex, Directory dir) {
        final float vertexX = vertex.getX();
        final float vertexY = vertex.getY();
        final float vertexZ = vertex.getZ();

        final int x = Maths.floor(vertexX);
        final int y = Maths.floor(vertexY);
        final int z = Maths.floor(vertexZ);
        return switch(dir) {
            case WEST ->  (
                this.getCachedLightLevel(-1, -1 + y, -1 + z) +
                this.getCachedLightLevel(-1,  0 + y, -1 + z) +
                this.getCachedLightLevel(-1, -1 + y,  0 + z) +
                this.getCachedLightLevel(-1,  0 + y,  0 + z)
            ) / 4F / BlockLightEngine.MAX_LEVEL;
            case EAST -> (
                this.getCachedLightLevel( 1, -1 + y, -1 + z) +
                this.getCachedLightLevel( 1,  0 + y, -1 + z) +
                this.getCachedLightLevel( 1, -1 + y,  0 + z) +
                this.getCachedLightLevel( 1,  0 + y,  0 + z)
            ) / 4F / BlockLightEngine.MAX_LEVEL;
            case DOWN -> (
                this.getCachedLightLevel(-1 + x, -1, -1 + z) +
                this.getCachedLightLevel( 0 + x, -1, -1 + z) +
                this.getCachedLightLevel(-1 + x, -1,  0 + z) +
                this.getCachedLightLevel( 0 + x, -1,  0 + z)
            ) / 4F / BlockLightEngine.MAX_LEVEL;
            case UP -> (
                this.getCachedLightLevel(-1 + x,  1, -1 + z) +
                this.getCachedLightLevel( 0 + x,  1, -1 + z) +
                this.getCachedLightLevel(-1 + x,  1,  0 + z) +
                this.getCachedLightLevel( 0 + x,  1,  0 + z)
            ) / 4F / BlockLightEngine.MAX_LEVEL;
            case SOUTH -> (
                this.getCachedLightLevel(-1 + x, -1 + y, -1) +
                this.getCachedLightLevel( 0 + x, -1 + y, -1) +
                this.getCachedLightLevel(-1 + x,  0 + y, -1) +
                this.getCachedLightLevel( 0 + x,  0 + y, -1)
            ) / 4F / BlockLightEngine.MAX_LEVEL;
            case NORTH -> (
                this.getCachedLightLevel(-1 + x, -1 + y, 1) +
                this.getCachedLightLevel( 0 + x, -1 + y, 1) +
                this.getCachedLightLevel(-1 + x,  0 + y, 1) +
                this.getCachedLightLevel( 0 + x,  0 + y, 1)
            ) / 4F / BlockLightEngine.MAX_LEVEL;
            default -> {
                final float[][][] blockVertices = new float[2][2][2];
                for(int i = 0; i < 2; i++) {
                    for(int j = 0; j < 2; j++) {
                        for(int k = 0; k < 2; k++) {
                            blockVertices[i][j][k] += lightLevelCache[i][j][k];
                            blockVertices[i][j][k] += lightLevelCache[i + 1][j][k];
                            blockVertices[i][j][k] += lightLevelCache[i][j + 1][k];
                            blockVertices[i][j][k] += lightLevelCache[i][j][k + 1];
                            blockVertices[i][j][k] += lightLevelCache[i + 1][j + 1][k];
                            blockVertices[i][j][k] += lightLevelCache[i][j + 1][k + 1];
                            blockVertices[i][j][k] += lightLevelCache[i + 1][j][k + 1];
                            blockVertices[i][j][k] += lightLevelCache[i + 1][j + 1][k + 1];
                        }
                    }
                }
                for(int i = 0; i < 2; i++)
                    for(int j = 0; j < 2; j++)
                        for(int k = 0; k < 2; k++)
                            blockVertices[i][j][k] /= 4F;

                final float x0 = blockVertices[0][0][0] * (1F - vertexX) + blockVertices[1][0][0] * vertexX;
                final float x1 = blockVertices[0][1][0] * (1F - vertexX) + blockVertices[1][1][0] * vertexX;

                final float y0 = x0 * (1F - vertexY) + x1 * vertexY;

                final float x2 = blockVertices[0][0][1] * (1F - vertexX) + blockVertices[1][0][1] * vertexX;
                final float x3 = blockVertices[0][1][1] * (1F - vertexX) + blockVertices[1][1][1] * vertexX;

                final float y1 = x2 * (1F - vertexY) + x3 * vertexY;

                final float z0 = y0 * (1F - vertexZ) + y1 * vertexZ;

                yield z0 / 2F / BlockLightEngine.MAX_LEVEL;
            }
        };
    }

    private void addFaces(int x, int y, int z, BlockStateModel model, Directory directory) {
        for(BlockFace face: model.getFacesGroup(directory)){

            // cache ambient occlusion
            final BlockVertex[] vertices = face.getVertices();
            for(int vertexIndex = 0; vertexIndex < vertices.length; vertexIndex++){
                final BlockVertex vertex = vertices[vertexIndex];
                aoCache[vertexIndex] = this.evaluateAmbientOcclusion(vertex, directory);
            }

            // add face
            final int beginDataIndex = verticesCache.size();
            verticesCache.add(face.getVertexArray());

            // correct face rotation for a right ao/light smoothing
            final boolean rotateFace = (aoCache[0] + aoCache[2]) > (aoCache[1] + aoCache[3]);
            final int[] rotatedIndices = BlockFace.VERTEX_INDEX_PERMUTATIONS[rotateFace ? 1 : 0];

            // correct vertices
            for(int i = 0; i < vertices.length; i++){
                // offsets
                final int cachePosIndex = (i * BlockVertex.SIZE + beginDataIndex);
                final int cacheTexcoordIndex = (cachePosIndex + BlockVertex.TEXCOORD_OFFSET);
                final int cacheColorIndex = (cachePosIndex + BlockVertex.COLOR_OFFSET);

                // rotated
                final int rotatedVertexIndex = rotatedIndices[i];
                final BlockVertex rotatedVertex = vertices[rotatedVertexIndex];

                // position
                verticesCache.set(cachePosIndex + 0, rotatedVertex.getX() + x);
                verticesCache.set(cachePosIndex + 1, rotatedVertex.getY() + y);
                verticesCache.set(cachePosIndex + 2, rotatedVertex.getZ() + z);

                // texcoord
                final ResourceAtlas atlas = context.registries().getAtlas("blocks");
                final TextureRegion region = atlas.getRegion(face.getTextureID());

                verticesCache.set(cacheTexcoordIndex + 0, (region.u1() + region.getWidth()  * rotatedVertex.getU()));
                verticesCache.set(cacheTexcoordIndex + 1, (region.v1() + region.getHeight() * rotatedVertex.getV()));

                // color
                final float ambientOcclusion = aoCache[rotatedVertexIndex];
                verticesCache.elementMul(cacheColorIndex + 0, ambientOcclusion);
                verticesCache.elementMul(cacheColorIndex + 1, ambientOcclusion);
                verticesCache.elementMul(cacheColorIndex + 2, ambientOcclusion);
                verticesCache.elementMul(cacheColorIndex + 3, 1F);
            }
        }
    }

    private void processTesselate(LevelChunk chunk) {
        // cache neighbor chunks
        chunkCache.cacheNeighborsFor(chunk);

        chunk.forEach((x, y, z) -> {
            // cache neighbor blocks
            final BlockState blockState = chunk.getBlockState(x, y, z);
            if(blockState == null || blockState.isBlockID("air"))
                return;

            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    for(int k = 0; k < 3; k++){
                        if(i == 1 && j == 1 && k == 1){
                            blockCache[1][1][1] = blockState;
                            continue;
                        }
                        final BlockState neighborBlock = chunkCache.getBlockState((x + i - 1), (y + j - 1), (z + k - 1));
                        blockCache[i][j][k] = neighborBlock;
                        final int lightLevel = chunkCache.getBlockLightLevel((x + i - 1), (y + j - 1), (z + k - 1));
                        lightLevelCache[i][j][k] = lightLevel;
                    }
                }
            }


            // add faces
            final BlockStateModel model = this.getBlockModel(blockState);

            // none faces
            this.addFaces(x, y, z, model, Directory.NONE);

            // east faces
            final BlockState blockStateEast = this.getCachedBlockState(Directory.EAST);
            if(blockStateEast == null || blockStateEast.isBlockID("air")){
                this.addFaces(x, y, z, model, Directory.EAST);

            }else{
                final BlockStateModel modelEast = this.getBlockModel(blockStateEast);
                if((blockStateEast == blockState && modelEast.isDontHidesSameBlockFaces()) || modelEast.isNotHidesOppositeFace(Directory.EAST))
                    this.addFaces(x, y, z, model, Directory.EAST);
            }

            // west faces
            final BlockState blockStateWest = this.getCachedBlockState(Directory.WEST);
            if(blockStateWest == null || blockStateWest.isBlockID("air")){
                this.addFaces(x, y, z, model, Directory.WEST);

            }else{
                final BlockStateModel modelWest = this.getBlockModel(blockStateWest);
                if((blockStateWest == blockState && modelWest.isDontHidesSameBlockFaces()) || modelWest.isNotHidesOppositeFace(Directory.WEST))
                    this.addFaces(x, y, z, model, Directory.WEST);
            }

            // up faces
            final BlockState blockStateUp = this.getCachedBlockState(Directory.UP);
            if(blockStateUp == null || blockStateUp.isBlockID("air")){
                this.addFaces(x, y, z, model, Directory.UP);

            }else{
                final BlockStateModel modelUp = this.getBlockModel(blockStateUp);
                if((blockStateUp == blockState && modelUp.isDontHidesSameBlockFaces()) || modelUp.isNotHidesOppositeFace(Directory.UP))
                    this.addFaces(x, y, z, model, Directory.UP);
            }

            // down faces
            final BlockState blockStateDown = this.getCachedBlockState(Directory.DOWN);
            if(blockStateDown == null || blockStateDown.isBlockID("air")){
                this.addFaces(x, y, z, model, Directory.DOWN);

            }else{
                final BlockStateModel modelDown = this.getBlockModel(blockStateDown);
                if((blockStateDown == blockState && modelDown.isDontHidesSameBlockFaces()) || modelDown.isNotHidesOppositeFace(Directory.DOWN))
                    this.addFaces(x, y, z, model, Directory.DOWN);
            }

            // north faces
            final BlockState blockStateNorth = this.getCachedBlockState(Directory.NORTH);
            if(blockStateNorth == null || blockStateNorth.isBlockID("air")){
                this.addFaces(x, y, z, model, Directory.NORTH);

            }else{
                final BlockStateModel modelNorth = this.getBlockModel(blockStateNorth);
                if((blockStateNorth == blockState && modelNorth.isDontHidesSameBlockFaces()) || modelNorth.isNotHidesOppositeFace(Directory.NORTH))
                    this.addFaces(x, y, z, model, Directory.NORTH);
            }

            // south faces
            final BlockState blockStateSouth = this.getCachedBlockState(Directory.SOUTH);
            if(blockStateSouth == null || blockStateSouth.isBlockID("air")){
                this.addFaces(x, y, z, model, Directory.SOUTH);

            }else{
                final BlockStateModel modelSouth = this.getBlockModel(blockStateSouth);
                if((blockStateSouth == blockState && modelSouth.isDontHidesSameBlockFaces()) || modelSouth.isNotHidesOppositeFace(Directory.SOUTH))
                    this.addFaces(x, y, z, model, Directory.SOUTH);
            }

        });

        chunk.freeMesh();
        final ChunkMesh mesh = meshCache.getFreeOrCreate();
        mesh.setData(verticesCache.arrayTrimmed());
        verticesCache.clear();
        chunk.setMesh(mesh);
    }

    @Override
    public void dispose() {
        meshCache.dispose();
    }

}
