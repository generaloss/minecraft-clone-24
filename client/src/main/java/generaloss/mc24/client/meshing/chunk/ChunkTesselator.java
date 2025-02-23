package generaloss.mc24.client.meshing.chunk;

import generaloss.mc24.client.meshing.block.BlockCache;
import generaloss.mc24.client.meshing.block.BlockFace;
import generaloss.mc24.client.meshing.block.BlockModel;
import generaloss.mc24.client.meshing.block.BlockVertex;
import generaloss.mc24.client.resources.handle.BlockModelHandle;
import generaloss.mc24.client.resources.ClientResources;
import generaloss.mc24.client.screen.ScreenGameSession;
import generaloss.mc24.server.common.Direction;
import generaloss.mc24.server.block.*;
import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.server.light.BlockLightEngine;
import generaloss.mc24.server.chunk.ChunkCache;
import jpize.util.array.FloatList;
import jpize.util.atlas.TextureAtlas;
import jpize.util.math.FastNoise;
import jpize.util.math.Maths;
import jpize.util.region.TextureRegion;
import jpize.util.time.Stopwatch;

import java.util.concurrent.ExecutorService;

public class ChunkTesselator {

    private final ChunkCache<LevelChunk> chunkCache;
    private final ChunkMeshCache meshCache;
    private volatile ChunkTesselatorStatus status;
    private final FloatList verticesCache;
    private final BlockCache blockCache;
    private final float[][] vertexLightCache;

    public ChunkTesselator(WorldLevel level, ChunkMeshCache meshCache) {
        this.chunkCache = new ChunkCache<>(level);
        this.meshCache = meshCache;
        this.status = ChunkTesselatorStatus.FREE;
        this.verticesCache = new FloatList();
        this.blockCache = new BlockCache();
        this.vertexLightCache = new float[4][BlockFace.VERTICES_NUMBER];
    }

    public ChunkTesselatorStatus getStatus() {
        return status;
    }

    private BlockModel getBlockModel(BlockState blockstate) {
        final BlockModelHandle handle = ClientResources.BLOCK_STATE_MODELS.get(blockstate);
        if(handle == null)
            return null;
        return handle.resource();
    }

    private float smoothLightForVertex(int colorChannel, BlockVertex vertex, Direction faceDirectory) {
        final int x = Maths.clamp(Math.round(vertex.getX()), 0, 1);
        final int y = Maths.clamp(Math.round(vertex.getY()), 0, 1);
        final int z = Maths.clamp(Math.round(vertex.getZ()), 0, 1);

        return switch(faceDirectory) {
            case WEST -> (
                blockCache.getLightLevel(-1, y, z, colorChannel) +
                blockCache.getLightLevel(-1, y - 1, z, colorChannel) +
                blockCache.getLightLevel(-1, y, z - 1, colorChannel) +
                blockCache.getLightLevel(-1, y - 1, z - 1, colorChannel) + 4
            ) * 0.25F / (BlockLightEngine.MAX_LEVEL + 1);
            case EAST -> (
                blockCache.getLightLevel(1, y, z, colorChannel) +
                blockCache.getLightLevel(1, y - 1, z, colorChannel) +
                blockCache.getLightLevel(1, y, z - 1, colorChannel) +
                blockCache.getLightLevel(1, y - 1, z - 1, colorChannel) + 4
            ) * 0.25F / (BlockLightEngine.MAX_LEVEL + 1);
            case DOWN -> (
                blockCache.getLightLevel(x, -1, z, colorChannel) +
                blockCache.getLightLevel(x - 1, -1, z, colorChannel) +
                blockCache.getLightLevel(x, -1, z - 1, colorChannel) +
                blockCache.getLightLevel(x - 1, -1, z - 1, colorChannel) + 4
            ) * 0.25F / (BlockLightEngine.MAX_LEVEL + 1);
            case UP -> (
                blockCache.getLightLevel(x, 1, z, colorChannel) +
                blockCache.getLightLevel(x - 1, 1, z, colorChannel) +
                blockCache.getLightLevel(x, 1, z - 1, colorChannel) +
                blockCache.getLightLevel(x - 1, 1, z - 1, colorChannel) + 4
            ) * 0.25F / (BlockLightEngine.MAX_LEVEL + 1);
            case SOUTH -> (
                blockCache.getLightLevel(x, y, -1, colorChannel) +
                blockCache.getLightLevel(x - 1, y, -1, colorChannel) +
                blockCache.getLightLevel(x, y - 1, -1, colorChannel) +
                blockCache.getLightLevel(x - 1, y - 1, -1, colorChannel) + 4
            ) * 0.25F / (BlockLightEngine.MAX_LEVEL + 1);
            case NORTH -> (
                blockCache.getLightLevel(x, y, 1, colorChannel) +
                blockCache.getLightLevel(x - 1, y, 1, colorChannel) +
                blockCache.getLightLevel(x, y - 1, 1, colorChannel) +
                blockCache.getLightLevel(x - 1, y - 1, 1, colorChannel) + 4
            ) * 0.25F / (BlockLightEngine.MAX_LEVEL + 1);
            default -> 1F;
        };
    }

    private void addFaces(int x, int y, int z, BlockModel model, Direction direction, boolean hide) {
        final boolean hasAmbientOcclusion = model.hasAmbientOcclusion();

        for(BlockFace face: model.getFacesGroup(direction)){
            if(hide && face.isMightBeCulled())
                continue;

            // cache ambient occlusion
            final BlockVertex[] vertices = face.getVertices();

            for(int vertexIndex = 0; vertexIndex < vertices.length; vertexIndex++){
                final BlockVertex vertex = vertices[vertexIndex];
                for(int channel = 0; channel < 4; channel++){
                    if(face.isSolid() && ScreenGameSession.AO){
                        vertexLightCache[channel][vertexIndex] = this.smoothLightForVertex(channel, vertex, direction);
                    }else{
                        vertexLightCache[channel][vertexIndex] = (Math.max(
                            blockCache.getLightLevel(direction.getX(), direction.getY(), direction.getZ(), channel),
                            blockCache.getLightLevel(0, 0, 0, channel)
                        ) + 1) / (BlockLightEngine.MAX_LEVEL + 1F);
                    }
                }
            }

            // add face
            final int beginDataIndex = verticesCache.size();
            verticesCache.add(face.getVertexArray());

            // correct vertices
            for(int i = 0; i < vertices.length; i++){
                // offsets
                final int cachePosIndex = (i * BlockVertex.SIZE + beginDataIndex);
                final int cacheTexcoordIndex = (cachePosIndex + BlockVertex.TEXCOORD_OFFSET);
                final int cacheColorIndex = (cachePosIndex + BlockVertex.COLOR_OFFSET);
                final int cacheBlocklightIndex = (cachePosIndex + BlockVertex.BLOCKLIGHT_OFFSET);
                final int cacheSkylightIndex = (cachePosIndex + BlockVertex.SKYLIGHT_OFFSET);

                // rotated
                final BlockVertex rotatedVertex = vertices[i];

                // position
                verticesCache.set(cachePosIndex + 0, rotatedVertex.getX() + x);
                verticesCache.set(cachePosIndex + 1, rotatedVertex.getY() + y);
                verticesCache.set(cachePosIndex + 2, rotatedVertex.getZ() + z);

                // texcoord
                final TextureAtlas<String> atlas = ClientResources.ATLASES.get("blocks").resource();
                final TextureRegion region = atlas.getRegion(face.getTextureID());

                verticesCache.set(cacheTexcoordIndex + 0, (region.u1() + region.getWidth()  * rotatedVertex.getU()));
                verticesCache.set(cacheTexcoordIndex + 1, (region.v1() + region.getHeight() * rotatedVertex.getV()));

                // color / tint
                if(face.getTintIndex() == 0){
                    final int chunkBlockX = chunkCache.getCenterChunk().position().getBlockX();
                    final int chunkBlockY = chunkCache.getCenterChunk().position().getBlockY();
                    final int chunkBlockZ = chunkCache.getCenterChunk().position().getBlockZ();
                    verticesCache.set(cacheColorIndex + 0, (noiseR.get(x + chunkBlockX, y + chunkBlockY, z + chunkBlockZ) * 0.5F + 0.5F) * 0.3F - 0.15F + 0.55F);
                    verticesCache.set(cacheColorIndex + 1, (noiseG.get(x + chunkBlockX, y + chunkBlockY, z + chunkBlockZ) * 0.5F + 0.5F) * 0.3F - 0.15F + 0.75F);
                    verticesCache.set(cacheColorIndex + 2, (noiseB.get(x + chunkBlockX, y + chunkBlockY, z + chunkBlockZ) * 0.5F + 0.5F) * 0.3F - 0.15F + 0.30F);
                }

                // block light
                verticesCache.set(cacheBlocklightIndex + 0, vertexLightCache[0][i]);
                verticesCache.set(cacheBlocklightIndex + 1, vertexLightCache[1][i]);
                verticesCache.set(cacheBlocklightIndex + 2, vertexLightCache[2][i]);

                // sky light
                verticesCache.set(cacheSkylightIndex, vertexLightCache[3][i]);
            }
        }
    }

    private final FastNoise noiseR = new FastNoise().setFrequency(1 / 64F).setSeed(1); //! temporary
    private final FastNoise noiseG = new FastNoise().setFrequency(1 / 64F).setSeed(2); //! temporary
    private final FastNoise noiseB = new FastNoise().setFrequency(1 / 64F).setSeed(3); //! temporary
    private final Stopwatch stopwatch = new Stopwatch().start(); //! debug

    private void tesselate(LevelChunk chunk) {
        stopwatch.reset();

        chunkCache.initFor(chunk);
        chunk.forEach((x, y, z) -> {
            // blockstate
            final BlockState blockstate = chunk.getBlockState(x, y, z);
            if(blockstate.isBlockID("air"))
                return;

            // model
            final BlockModel model = this.getBlockModel(blockstate);
            if(model == null)
                return;

            blockCache.initFor(x, y, z, blockstate, chunkCache);

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

        // System.out.println("[INFO]: Mesh build time: " + stopwatch.getMillis());
        status = ChunkTesselatorStatus.DONE;
    }

    public void tesselate(LevelChunk chunk, ExecutorService executorService) {
        status = ChunkTesselatorStatus.WORKING;
        executorService.execute(() -> this.tesselate(chunk));
    }

    public void unlock() {
        final LevelChunk chunk = chunkCache.getCenterChunk();
        chunk.freeMesh();

        final float[] array = verticesCache.arrayTrimmed();
        if(array.length != 0){
            final ChunkMesh mesh = meshCache.getFreeOrCreate();
            mesh.setData(array);
            chunk.setMesh(mesh);
        }
        verticesCache.clear();
        status = ChunkTesselatorStatus.FREE;
    }

}
