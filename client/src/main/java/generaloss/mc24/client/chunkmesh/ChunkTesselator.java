package generaloss.mc24.client.chunkmesh;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.block.BlockFace;
import generaloss.mc24.client.block.BlockStateModel;
import generaloss.mc24.client.block.BlockVertex;
import generaloss.mc24.server.Directory;
import generaloss.mc24.server.block.*;
import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.server.world.BlockLightEngine;
import generaloss.mc24.server.chunk.ChunkCache;
import jpize.util.array.FloatList;
import jpize.util.atlas.TextureAtlas;
import jpize.util.math.Maths;
import jpize.util.region.TextureRegion;
import jpize.util.time.Stopwatch;

import java.util.concurrent.ExecutorService;

public class ChunkTesselator {

    private final Main context;

    private final ChunkCache<WorldLevel, LevelChunk> chunkCache;
    private final ChunkMeshCache meshCache;
    private volatile TesselatorStatus status;
    private final FloatList verticesCache;
    private final BlockAndLightCache blockAndLightCache;
    private final float[][] smoothLightCache;

    public ChunkTesselator(Main context, WorldLevel level, ChunkMeshCache meshCache) {
        this.context = context;

        this.chunkCache = new ChunkCache<>(level);
        this.meshCache = meshCache;
        this.status = TesselatorStatus.FREE;
        this.verticesCache = new FloatList();
        this.blockAndLightCache = new BlockAndLightCache();
        this.smoothLightCache = new float[3][BlockFace.VERTICES_NUMBER];
    }

    public TesselatorStatus getStatus() {
        return status;
    }

    private BlockStateModel getBlockModel(BlockState blockstate) {
        return context.registries().BLOCK_MODELS.get(blockstate);
    }

    private float smoothLight(int channel, BlockVertex vertex, Directory dir) {
        final float vertexX = vertex.getX();
        final float vertexY = vertex.getY();
        final float vertexZ = vertex.getZ();

        final int x = Maths.floor(vertexX);
        final int y = Maths.floor(vertexY);
        final int z = Maths.floor(vertexZ);
        return switch(dir) {
            case WEST ->  (
                blockAndLightCache.getLightLevel(-1, -1 + y, -1 + z, channel) +
                blockAndLightCache.getLightLevel(-1,  0 + y, -1 + z, channel) +
                blockAndLightCache.getLightLevel(-1, -1 + y,  0 + z, channel) +
                blockAndLightCache.getLightLevel(-1,  0 + y,  0 + z, channel)
            ) / 4F / BlockLightEngine.MAX_LEVEL;
            case EAST -> (
                blockAndLightCache.getLightLevel( 1, -1 + y, -1 + z, channel) +
                blockAndLightCache.getLightLevel( 1,  0 + y, -1 + z, channel) +
                blockAndLightCache.getLightLevel( 1, -1 + y,  0 + z, channel) +
                blockAndLightCache.getLightLevel( 1,  0 + y,  0 + z, channel)
            ) / 4F / BlockLightEngine.MAX_LEVEL;
            case DOWN -> (
                blockAndLightCache.getLightLevel(-1 + x, -1, -1 + z, channel) +
                blockAndLightCache.getLightLevel( 0 + x, -1, -1 + z, channel) +
                blockAndLightCache.getLightLevel(-1 + x, -1,  0 + z, channel) +
                blockAndLightCache.getLightLevel( 0 + x, -1,  0 + z, channel)
            ) / 4F / BlockLightEngine.MAX_LEVEL;
            case UP -> (
                blockAndLightCache.getLightLevel(-1 + x,  1, -1 + z, channel) +
                blockAndLightCache.getLightLevel( 0 + x,  1, -1 + z, channel) +
                blockAndLightCache.getLightLevel(-1 + x,  1,  0 + z, channel) +
                blockAndLightCache.getLightLevel( 0 + x,  1,  0 + z, channel)
            ) / 4F / BlockLightEngine.MAX_LEVEL;
            case SOUTH -> (
                blockAndLightCache.getLightLevel(-1 + x, -1 + y, -1, channel) +
                blockAndLightCache.getLightLevel( 0 + x, -1 + y, -1, channel) +
                blockAndLightCache.getLightLevel(-1 + x,  0 + y, -1, channel) +
                blockAndLightCache.getLightLevel( 0 + x,  0 + y, -1, channel)
            ) / 4F / BlockLightEngine.MAX_LEVEL;
            case NORTH -> (
                blockAndLightCache.getLightLevel(-1 + x, -1 + y, 1, channel) +
                blockAndLightCache.getLightLevel( 0 + x, -1 + y, 1, channel) +
                blockAndLightCache.getLightLevel(-1 + x,  0 + y, 1, channel) +
                blockAndLightCache.getLightLevel( 0 + x,  0 + y, 1, channel)
            ) / 4F / BlockLightEngine.MAX_LEVEL;
            default -> {
                final float[][][] blockVertices = new float[2][2][2];

                for(int i = 0; i < 2; i++) {
                    final int i0 = (i - 1);

                    for(int j = 0; j < 2; j++) {
                        final int j0 = (j - 1);

                        for(int k = 0; k < 2; k++) {
                            final int k0 = (k - 1);

                            blockVertices[i][j][k] += blockAndLightCache.getLightLevel(i0, j0, k0, channel);
                            blockVertices[i][j][k] += blockAndLightCache.getLightLevel(i , j0, k0, channel);
                            blockVertices[i][j][k] += blockAndLightCache.getLightLevel(i0, j , k0, channel);
                            blockVertices[i][j][k] += blockAndLightCache.getLightLevel(i , j , k0, channel);
                            blockVertices[i][j][k] += blockAndLightCache.getLightLevel(i0, j0, k , channel);
                            blockVertices[i][j][k] += blockAndLightCache.getLightLevel(i , j0, k , channel);
                            blockVertices[i][j][k] += blockAndLightCache.getLightLevel(i0, j , k , channel);
                            blockVertices[i][j][k] += blockAndLightCache.getLightLevel(i , j , k , channel);
                        }
                    }
                }
                for(int i = 0; i < 2; i++)
                    for(int j = 0; j < 2; j++)
                        for(int k = 0; k < 2; k++)
                            blockVertices[i][j][k] /= 8F;

                final float vertexInvX = (1F - vertexX);
                final float vertexInvY = (1F - vertexY);
                final float vertexInvZ = (1F - vertexZ);

                // final float x_y1_z1 = (blockVertices[0][0][0] * vertexInvX  +  blockVertices[1][0][0] * vertexX);
                // final float x_y2_z1 = (blockVertices[0][1][0] * vertexInvX  +  blockVertices[1][1][0] * vertexX);
                // final float x_y_z1 = (x_y1_z1 * vertexInvY  +  x_y2_z1 * vertexY);
                // final float x_y1_z2 = (blockVertices[0][0][1] * vertexInvX  +  blockVertices[1][0][1] * vertexX);
                // final float x_y2_z2 = (blockVertices[0][1][1] * vertexInvX  +  blockVertices[1][1][1] * vertexX);
                // final float x_y_z2 = (x_y1_z2 * vertexInvY  +  x_y2_z2 * vertexY);
                // final float x_y_z = (x_y_z1 * vertexInvZ  +  x_y_z2 * vertexZ);

                if(a) {
                    System.out.println("[" + vertexX + ", " + vertexY + ", " + vertexZ + "] = " + blockVertices[0][0][0] + " | " + blockVertices[0][1][0]);
                }

                final float x_y_z = (blockVertices[0][0][0] * vertexInvY  +  blockVertices[0][1][0] * vertexY);
                // final float x_y_z = (x_y_z * vertexInvY  +  x_y_z * vertexY);
                // final float x_y_z = (blockVertices[0][0][0] * vertexInvX  +  blockVertices[0][0][0] * vertexX);

                yield x_y_z / BlockLightEngine.MAX_LEVEL;
            }
        };
    }

    private boolean a;

    private void addFaces(int x, int y, int z, BlockStateModel model, Directory directory) {
        a = model.getBlockState().isBlockID("stairs");
        for(BlockFace face: model.getFacesGroup(directory)){

            // cache ambient occlusion
            final BlockVertex[] vertices = face.getVertices();

            for(int vertexIndex = 0; vertexIndex < vertices.length; vertexIndex++){
                final BlockVertex vertex = vertices[vertexIndex];
                for(int channel = 0; channel < 3; channel++)
                    smoothLightCache[channel][vertexIndex] = this.smoothLight(channel, vertex, directory);
            }

            // add face
            final int beginDataIndex = verticesCache.size();
            verticesCache.add(face.getVertexArray());

            // correct face rotation for a right ao/light smoothing
            // final boolean rotateFace = false; //(aoCache[0] + aoCache[2] > aoCache[1] + aoCache[3]);
            // final int[] rotatedIndices = BlockFace.VERTEX_INDEX_PERMUTATIONS[rotateFace ? 1 : 0];

            // correct vertices
            for(int i = 0; i < vertices.length; i++){
                // offsets
                final int cachePosIndex = (i * BlockVertex.SIZE + beginDataIndex);
                final int cacheTexcoordIndex = (cachePosIndex + BlockVertex.TEXCOORD_OFFSET);
                final int cacheColorIndex = (cachePosIndex + BlockVertex.COLOR_OFFSET);
                final int cacheLightIndex = (cachePosIndex + BlockVertex.LIGHT_OFFSET);

                // rotated
                final int rotatedVertexIndex = i; // rotatedIndices[i];
                final BlockVertex rotatedVertex = vertices[rotatedVertexIndex];

                // position
                verticesCache.set(cachePosIndex + 0, rotatedVertex.getX() + x);
                verticesCache.set(cachePosIndex + 1, rotatedVertex.getY() + y);
                verticesCache.set(cachePosIndex + 2, rotatedVertex.getZ() + z);

                // texcoord
                final TextureAtlas<String> atlas = context.registries().ATLASES.get("blocks");
                final TextureRegion region = atlas.getRegion(face.getTextureID());

                verticesCache.set(cacheTexcoordIndex + 0, (region.u1() + region.getWidth()  * rotatedVertex.getU()));
                verticesCache.set(cacheTexcoordIndex + 1, (region.v1() + region.getHeight() * rotatedVertex.getV()));

                // rgb light
                verticesCache.set(cacheLightIndex + 0, smoothLightCache[0][rotatedVertexIndex]);
                verticesCache.set(cacheLightIndex + 1, smoothLightCache[1][rotatedVertexIndex]);
                verticesCache.set(cacheLightIndex + 2, smoothLightCache[2][rotatedVertexIndex]);
            }
        }
    }

    private final Stopwatch stopwatch = new Stopwatch().start();

    private void tesselate(LevelChunk chunk) {
        stopwatch.reset();

        // cache neighbor chunks
        chunkCache.cacheNeighborsFor(chunk);

        chunk.forEach((x, y, z) -> {
            // cache neighbor blocks
            final BlockState blockstate = chunk.getBlockState(x, y, z);
            if(blockstate.isBlockID("air"))
                return;

            blockAndLightCache.cacheNeighborsFor(x, y, z, blockstate, chunkCache);

            // add faces
            final BlockStateModel model = this.getBlockModel(blockstate);
            if(model == null)
                return;

            // none faces
            this.addFaces(x, y, z, model, Directory.NONE);

            // east faces
            final BlockState blockstateEast = blockAndLightCache.getBlockState(Directory.EAST);
            if(blockstateEast.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Directory.EAST);

            }else{
                final BlockStateModel modelEast = this.getBlockModel(blockstateEast);
                if((blockstateEast == blockstate && modelEast.isDontHidesSameBlockFaces()) || modelEast.isNotHidesOppositeFace(Directory.EAST))
                    this.addFaces(x, y, z, model, Directory.EAST);
            }

            // west faces
            final BlockState blockstateWest = blockAndLightCache.getBlockState(Directory.WEST);
            if(blockstateWest.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Directory.WEST);

            }else{
                final BlockStateModel modelWest = this.getBlockModel(blockstateWest);
                if((blockstateWest == blockstate && modelWest.isDontHidesSameBlockFaces()) || modelWest.isNotHidesOppositeFace(Directory.WEST))
                    this.addFaces(x, y, z, model, Directory.WEST);
            }

            // up faces
            final BlockState blockstateUp = blockAndLightCache.getBlockState(Directory.UP);
            if(blockstateUp.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Directory.UP);

            }else{
                final BlockStateModel modelUp = this.getBlockModel(blockstateUp);
                if((blockstateUp == blockstate && modelUp.isDontHidesSameBlockFaces()) || modelUp.isNotHidesOppositeFace(Directory.UP))
                    this.addFaces(x, y, z, model, Directory.UP);
            }

            // down faces
            final BlockState blockstateDown = blockAndLightCache.getBlockState(Directory.DOWN);
            if(blockstateDown.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Directory.DOWN);

            }else{
                final BlockStateModel modelDown = this.getBlockModel(blockstateDown);
                if((blockstateDown == blockstate && modelDown.isDontHidesSameBlockFaces()) || modelDown.isNotHidesOppositeFace(Directory.DOWN))
                    this.addFaces(x, y, z, model, Directory.DOWN);
            }

            // north faces
            final BlockState blockstateNorth = blockAndLightCache.getBlockState(Directory.NORTH);
            if(blockstateNorth.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Directory.NORTH);

            }else{
                final BlockStateModel modelNorth = this.getBlockModel(blockstateNorth);
                if((blockstateNorth == blockstate && modelNorth.isDontHidesSameBlockFaces()) || modelNorth.isNotHidesOppositeFace(Directory.NORTH))
                    this.addFaces(x, y, z, model, Directory.NORTH);
            }

            // south faces
            final BlockState blockstateSouth = blockAndLightCache.getBlockState(Directory.SOUTH);
            if(blockstateSouth.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Directory.SOUTH);

            }else{
                final BlockStateModel modelSouth = this.getBlockModel(blockstateSouth);
                if((blockstateSouth == blockstate && modelSouth.isDontHidesSameBlockFaces()) || modelSouth.isNotHidesOppositeFace(Directory.SOUTH))
                    this.addFaces(x, y, z, model, Directory.SOUTH);
            }
        });

        System.out.println("[INFO]: Mesh build time: " + stopwatch.getMillis());
        status = TesselatorStatus.DONE;
    }

    public void tesselate(LevelChunk chunk, ExecutorService executorService) {
        status = TesselatorStatus.WORKING;
        executorService.execute(() -> this.tesselate(chunk));
    }

    public void unlock() {
        final LevelChunk chunk = chunkCache.getCenterChunk();
        chunk.freeMesh();

        final ChunkMesh mesh = meshCache.getFreeOrCreate();
        mesh.setData(verticesCache.arrayTrimmed());

        verticesCache.clear();
        chunk.setMesh(mesh);

        status = TesselatorStatus.FREE;
    }

}
