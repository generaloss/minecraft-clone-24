package generaloss.mc24.client.chunkmesh;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.block.BlockFace;
import generaloss.mc24.client.block.BlockModel;
import generaloss.mc24.client.block.BlockVertex;
import generaloss.mc24.client.screen.SessionScreen;
import generaloss.mc24.server.Direction;
import generaloss.mc24.server.block.*;
import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.server.world.BlockLightEngine;
import generaloss.mc24.server.chunk.ChunkCache;
import jpize.util.array.FloatList;
import jpize.util.atlas.TextureAtlas;
import jpize.util.math.FastNoise;
import jpize.util.region.TextureRegion;
import jpize.util.time.Stopwatch;

import java.util.concurrent.ExecutorService;

public class ChunkTesselator {

    private final Main context;

    private final ChunkCache<WorldLevel, LevelChunk> chunkCache;
    private final ChunkMeshCache meshCache;
    private volatile TesselatorStatus status;
    private final FloatList verticesCache;
    private final BlockAndLightCache blockCache;
    private final float[][] smoothLightCache;

    public ChunkTesselator(Main context, WorldLevel level, ChunkMeshCache meshCache) {
        this.context = context;

        this.chunkCache = new ChunkCache<>(level);
        this.meshCache = meshCache;
        this.status = TesselatorStatus.FREE;
        this.verticesCache = new FloatList();
        this.blockCache = new BlockAndLightCache();
        this.smoothLightCache = new float[3][BlockFace.VERTICES_NUMBER];
    }

    public TesselatorStatus getStatus() {
        return status;
    }

    private BlockModel getBlockModel(BlockState blockstate) {
        return context.registries().BLOCK_STATE_MODELS.get(blockstate);
    }

    private float calculateVertexLight(int channel, BlockVertex vertex, Direction dir) {
        final float x = vertex.getX();
        final float y = vertex.getY();
        final float z = vertex.getZ();
        final float invx = (1F - x);
        final float invy = (1F - y);
        final float invz = (1F - z);

        final float[][][] verticesLight = new float[2][2][2];
        for(int i1 = 0; i1 < 2; i1++) {
            final int i0 = (i1 - 1);
            for(int j1 = 0; j1 < 2; j1++) {
                final int j0 = (j1 - 1);
                for(int k1 = 0; k1 < 2; k1++) {
                    final int k0 = (k1 - 1);
                    verticesLight[i1][j1][k1] += (
                        blockCache.getLightLevel(i0, j0, k0, channel) +
                        blockCache.getLightLevel(i1, j0, k0, channel) +
                        blockCache.getLightLevel(i0, j1, k0, channel) +
                        blockCache.getLightLevel(i1, j1, k0, channel) +
                        blockCache.getLightLevel(i0, j0, k1, channel) +
                        blockCache.getLightLevel(i1, j0, k1, channel) +
                        blockCache.getLightLevel(i0, j1, k1, channel) +
                        blockCache.getLightLevel(i1, j1, k1, channel)
                    ) * 0.125F / BlockLightEngine.MAX_LEVEL;
                }
            }
        }

        return switch(dir) {
            case WEST -> {
                final float i1_j1_k = (verticesLight[0][0][0] * invz  +  z * verticesLight[0][0][1]);
                final float i2_j1_k = (verticesLight[0][1][0] * invz  +  z * verticesLight[0][1][1]);
                final float i_j1_k = (i1_j1_k * invy  +  y * i2_j1_k);
                if(x != 0F){
                    final float i1_j2_k = (verticesLight[1][0][0] * invz + z * verticesLight[1][0][1]);
                    final float i2_j2_k = (verticesLight[1][1][0] * invz + z * verticesLight[1][1][1]);
                    final float i_j2_k = (i1_j2_k * invy + y * i2_j2_k);

                    yield (i_j1_k * invx  +  x * i_j2_k);
                }
                yield i_j1_k;
            }
            case EAST -> {
                final float i1_j2_k = (verticesLight[1][0][0] * invz  +  z * verticesLight[1][0][1]);
                final float i2_j2_k = (verticesLight[1][1][0] * invz  +  z * verticesLight[1][1][1]);
                final float i_j2_k = (i1_j2_k * invy  +  y * i2_j2_k);

                if(x != 1F) {
                    final float i1_j1_k = (verticesLight[0][0][0] * invz  +  z * verticesLight[0][0][1]);
                    final float i2_j1_k = (verticesLight[0][1][0] * invz  +  z * verticesLight[0][1][1]);
                    final float i_j1_k = (i1_j1_k * invy  +  y * i2_j1_k);

                    yield (i_j1_k * invx  +  x * i_j2_k);
                }

                yield i_j2_k;
            }
            case DOWN -> {
                final float i1_j1_k = (verticesLight[0][0][0] * invz  +  z * verticesLight[0][0][1]);
                final float i2_j1_k = (verticesLight[1][0][0] * invz  +  z * verticesLight[1][0][1]);
                final float i_j1_k = (i1_j1_k * invx  +  x * i2_j1_k);

                if(y != 0F){
                    final float i1_j2_k = (verticesLight[0][1][0] * invz + z * verticesLight[0][1][1]);
                    final float i2_j2_k = (verticesLight[1][1][0] * invz + z * verticesLight[1][1][1]);
                    final float i_j2_k = (i1_j2_k * invx + x * i2_j2_k);

                    yield (i_j1_k * invy  +  y * i_j2_k);
                }

                yield i_j1_k;
            }
            case UP -> {
                final float i1_j2_k = (verticesLight[0][1][0] * invz  +  z * verticesLight[0][1][1]);
                final float i2_j2_k = (verticesLight[1][1][0] * invz  +  z * verticesLight[1][1][1]);
                final float i_j2_k = (i1_j2_k * invx  +  x * i2_j2_k);

                if(y != 1F) {
                    final float i1_j1_k = (verticesLight[0][0][0] * invz  +  z * verticesLight[0][0][1]);
                    final float i2_j1_k = (verticesLight[1][0][0] * invz  +  z * verticesLight[1][0][1]);
                    final float i_j1_k = (i1_j1_k * invx  +  x * i2_j1_k);

                    yield (i_j1_k * invy  +  y * i_j2_k);
                }

                yield i_j2_k;
            }
            case SOUTH -> {
                final float i1_j1_k = (verticesLight[0][0][0] * invy  +  y * verticesLight[0][1][0]);
                final float i2_j1_k = (verticesLight[1][0][0] * invy  +  y * verticesLight[1][1][0]);
                final float i_j1_k = (i1_j1_k * invx  +  x * i2_j1_k);

                if(z != 0F){
                    final float i1_j2_k = (verticesLight[0][0][1] * invy + y * verticesLight[0][1][1]);
                    final float i2_j2_k = (verticesLight[1][0][1] * invy + y * verticesLight[1][1][1]);
                    final float i_j2_k = (i1_j2_k * invx + x * i2_j2_k);

                    yield (i_j1_k * invz  +  z * i_j2_k);
                }

                yield i_j1_k;
            }
            case NORTH -> {
                final float i1_j2_k = (verticesLight[0][0][1] * invy  +  y * verticesLight[0][1][1]);
                final float i2_j2_k = (verticesLight[1][0][1] * invy  +  y * verticesLight[1][1][1]);
                final float i_j2_k = (i1_j2_k * invx  +  x * i2_j2_k);

                if(z != 1F) {
                    final float i1_j1_k = (verticesLight[0][0][0] * invy  +  y * verticesLight[0][1][0]);
                    final float i2_j1_k = (verticesLight[1][0][0] * invy  +  y * verticesLight[1][1][0]);
                    final float i_j1_k = (i1_j1_k * invx  +  x * i2_j1_k);

                    yield (i_j1_k * invz  +  z * i_j2_k);
                }

                yield i_j2_k;
            }
            default -> 1F;
        };
    }

    private void addFaces(int x, int y, int z, BlockModel model, Direction direction, boolean hide) {
        final boolean hasAmbientOcclusion = model.hasAmbientOcclusion();

        for(BlockFace face: model.getFacesGroup(direction)){
            if(hide && face.getCullBy() == direction)
                continue;

            // cache ambient occlusion
            final BlockVertex[] vertices = face.getVertices();

            for(int vertexIndex = 0; vertexIndex < vertices.length; vertexIndex++){
                final BlockVertex vertex = vertices[vertexIndex];
                for(int channel = 0; channel < 3; channel++){
                    try{
                        if(SessionScreen.AO && face.isSolid()){
                            smoothLightCache[channel][vertexIndex] =
                                this.calculateVertexLight(channel, vertex, direction);
                        }else{
                            smoothLightCache[channel][vertexIndex] = (float) (
                                blockCache.getLightLevel(direction.getX(), direction.getY(), direction.getZ(), channel)
                                + blockCache.getLightLevel(0, 0, 0, channel)
                            ) / BlockLightEngine.MAX_LEVEL;
                        }
                    }catch(Exception ignored){
                        System.err.println("[ERROR] Failed to smooth light");
                    }
                }
            }

            // add face
            final int beginDataIndex = verticesCache.size();
            verticesCache.add(face.getVertexArray());

            // correct face rotation for a right ao/light smoothing
            // final boolean rotateFace = (smoothLightCache[1][0] + smoothLightCache[1][2] > smoothLightCache[1][1] + smoothLightCache[1][3]);
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

                // color
                float red   = smoothLightCache[0][rotatedVertexIndex];
                float green = smoothLightCache[1][rotatedVertexIndex];
                float blue  = smoothLightCache[2][rotatedVertexIndex];

                // tint
                if(face.getTintIndex() == 0){
                    final int chunkBlockX = chunkCache.getCenterChunk().position().getBlockX();
                    final int chunkBlockY = chunkCache.getCenterChunk().position().getBlockY();
                    final int chunkBlockZ = chunkCache.getCenterChunk().position().getBlockZ();
                    red   *= (noiseR.get(x + chunkBlockX, y + chunkBlockY, z + chunkBlockZ) * 0.5F + 0.5F) * 0.3F - 0.15F + 0.55F; // 0.55F;
                    green *= (noiseG.get(x + chunkBlockX, y + chunkBlockY, z + chunkBlockZ) * 0.5F + 0.5F) * 0.3F - 0.15F + 0.75F; // 0.75F;
                    blue  *= (noiseB.get(x + chunkBlockX, y + chunkBlockY, z + chunkBlockZ) * 0.5F + 0.5F) * 0.3F - 0.15F + 0.3F;  // 0.3F;
                }

                verticesCache.set(cacheLightIndex + 0, red  );
                verticesCache.set(cacheLightIndex + 1, green);
                verticesCache.set(cacheLightIndex + 2, blue );
            }
        }
    }

    private final FastNoise noiseR = new FastNoise().setFrequency(1 / 64F).setSeed(1); //! temporary
    private final FastNoise noiseG = new FastNoise().setFrequency(1 / 64F).setSeed(2); //! temporary
    private final FastNoise noiseB = new FastNoise().setFrequency(1 / 64F).setSeed(3); //! temporary
    private final Stopwatch stopwatch = new Stopwatch().start(); //! debug

    private void tesselate(LevelChunk chunk) {
        stopwatch.reset();

        chunkCache.cacheNeighborsFor(chunk);
        chunk.forEach((x, y, z) -> {
            // blockstate
            final BlockState blockstate = chunk.getBlockState(x, y, z);
            if(blockstate.isBlockID("air"))
                return;

            // model
            final BlockModel model = this.getBlockModel(blockstate);
            if(model == null)
                return;

            blockCache.cacheNeighborsFor(x, y, z, blockstate, chunkCache);

            // add none faces
            this.addFaces(x, y, z, model, Direction.NONE, false);

            // add east faces
            final BlockState blockstateEast = blockCache.getBlockState(Direction.EAST);
            if(blockstateEast.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Direction.EAST, false);

            }else{
                final BlockModel modelEast = this.getBlockModel(blockstateEast);
                this.addFaces(x, y, z, model, Direction.EAST, modelEast.isHidesOthersFace(Direction.EAST));
            }

            // add west faces
            final BlockState blockstateWest = blockCache.getBlockState(Direction.WEST);
            if(blockstateWest.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Direction.WEST, false);

            }else{
                final BlockModel modelWest = this.getBlockModel(blockstateWest);
                this.addFaces(x, y, z, model, Direction.WEST, modelWest.isHidesOthersFace(Direction.WEST));
            }

            // add up faces
            final BlockState blockstateUp = blockCache.getBlockState(Direction.UP);
            if(blockstateUp.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Direction.UP, false);

            }else{
                final BlockModel modelUp = this.getBlockModel(blockstateUp);
                this.addFaces(x, y, z, model, Direction.UP, modelUp.isHidesOthersFace(Direction.UP));
            }

            // add down faces
            final BlockState blockstateDown = blockCache.getBlockState(Direction.DOWN);
            if(blockstateDown.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Direction.DOWN, false);

            }else{
                final BlockModel modelDown = this.getBlockModel(blockstateDown);
                this.addFaces(x, y, z, model, Direction.DOWN, modelDown.isHidesOthersFace(Direction.DOWN));
            }

            // add north faces
            final BlockState blockstateNorth = blockCache.getBlockState(Direction.NORTH);
            if(blockstateNorth.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Direction.NORTH, false);

            }else{
                final BlockModel modelNorth = this.getBlockModel(blockstateNorth);
                this.addFaces(x, y, z, model, Direction.NORTH, modelNorth.isHidesOthersFace(Direction.NORTH));
            }

            // add south faces
            final BlockState blockstateSouth = blockCache.getBlockState(Direction.SOUTH);
            if(blockstateSouth.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Direction.SOUTH, false);

            }else{
                final BlockModel modelSouth = this.getBlockModel(blockstateSouth);
                this.addFaces(x, y, z, model, Direction.SOUTH, modelSouth.isHidesOthersFace(Direction.SOUTH));
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
