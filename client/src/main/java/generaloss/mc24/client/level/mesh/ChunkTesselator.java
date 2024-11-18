package generaloss.mc24.client.level.mesh;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.block.BlockFace;
import generaloss.mc24.client.block.BlockModel;
import generaloss.mc24.client.block.BlockModelRegistry;
import generaloss.mc24.client.block.BlockVertex;
import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.client.resource.ResourceAtlas;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.common.Directory;
import jpize.app.Jpize;
import jpize.util.Disposable;
import jpize.util.array.FloatList;
import jpize.util.math.Mathc;
import jpize.util.math.Maths;
import jpize.util.region.TextureRegion;
import jpize.util.time.Stopwatch;

import java.util.LinkedList;
import java.util.Queue;

public class ChunkTesselator implements Disposable {

    private final Main context;
    private final WorldLevel level;
    private final Queue<LevelChunk> taskQueue;

    private final ChunkMeshCache meshCache;
    private final FloatList verticesCache;
    private final LevelChunk[][][] chunkCache;
    private final int[][][] blockCache;
    private final float[][][] ambientOcclusionCache;

    public ChunkTesselator(Main context, WorldLevel level) {
        this.context = context;
        this.level = level;
        this.taskQueue = new LinkedList<>();
        this.meshCache = new ChunkMeshCache();
        this.verticesCache = new FloatList();
        this.chunkCache = new LevelChunk[3][3][3];
        this.blockCache = new int[3][3][3];
        this.ambientOcclusionCache = new float[6][3][3];
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

            if(timer.getMillis() > Jpize.getDeltaTime() * 900)
                break;
        }
    }

    private LevelChunk getCachedChunk(int i, int j, int k) {
        return chunkCache[i + 1][j + 1][k + 1];
    }

    private int getCachedBlockID(int i, int j, int k) {
        return blockCache[i + 1][j + 1][k + 1];
    }

    private int getCachedBlockID(Directory dir) {
        return this.getCachedBlockID(dir.getX(), dir.getY(), dir.getZ());
    }

    private int getBlockID(int x, int y, int z) {
        final int chunkY = Mathc.signum(Maths.floor((float) y / LevelChunk.SIZE));
        final int chunkX = Mathc.signum(Maths.floor((float) x / LevelChunk.SIZE));
        final int chunkZ = Mathc.signum(Maths.floor((float) z / LevelChunk.SIZE));
        final LevelChunk chunk = this.getCachedChunk(chunkX, chunkY, chunkZ);
        if(chunk == null)
            return 0;

        final int norX = (x - chunkX * LevelChunk.SIZE);
        final int norY = (y - chunkY * LevelChunk.SIZE);
        final int norZ = (z - chunkZ * LevelChunk.SIZE);
        return chunk.getBlock(norX, norY, norZ);
    }


    private void addFaces(int x, int y, int z, BlockModel model, Directory directory) {
        // cache ambient occlusion
        // [...]

        for(BlockFace face: model.getFacesGroup(directory)){
            // add face
            final int beginDataIndex = verticesCache.size();
            verticesCache.add(face.getVertexDataArray());

            // correct position
            for(int beginVertexIndex: face.getIndices()){
                final int index = (beginDataIndex + beginVertexIndex);

                // color
                final float vx = verticesCache.get(index + 0);
                final float vx1 = 1F - vx;
                final float vy = verticesCache.get(index + 1);
                final float vz = verticesCache.get(index + 2);
                final float vz1 = 1F - vz;

                float grayscale = 1F;
                // [...]

                verticesCache.valMul(index + BlockVertex.COLOR_OFFSET + 0, grayscale);
                verticesCache.valMul(index + BlockVertex.COLOR_OFFSET + 1, grayscale);
                verticesCache.valMul(index + BlockVertex.COLOR_OFFSET + 2, grayscale);
                verticesCache.valMul(index + BlockVertex.COLOR_OFFSET + 3, 1);

                // texcoord
                final ResourceAtlas atlas = context.resources().get("block_atlas");
                final TextureRegion region = atlas.getRegion(face.getTextureID());
                if(region != null){
                    final float u = (region.u1() + region.getWidth()  * verticesCache.get(index + BlockVertex.TEXCOORD_OFFSET + 0));
                    final float v = (region.v1() + region.getHeight() * verticesCache.get(index + BlockVertex.TEXCOORD_OFFSET + 1));
                    verticesCache.set(index + BlockVertex.TEXCOORD_OFFSET + 0, u);
                    verticesCache.set(index + BlockVertex.TEXCOORD_OFFSET + 1, v);
                }

                // position
                verticesCache.valAdd(index + 0, x);
                verticesCache.valAdd(index + 1, y);
                verticesCache.valAdd(index + 2, z);
            }
        }
    }

    private void processTesselate(LevelChunk chunk) {
        // cache neighbor chunks
        final ChunkPos chunkPosition = chunk.getPosition();

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                for(int k = 0; k < 3; k++){
                    if(i == 1 && j == 1 && k == 1){
                        chunkCache[1][1][1] = chunk;
                        continue;
                    }
                    chunkCache[i][j][k] = level.getChunk(chunkPosition.getNeighbor(i - 1, j - 1, k - 1));
                }
            }
        }


        chunk.forEachBlock((x, y, z) -> {
            // cache neighbor blocks
            final int blockID = chunk.getBlock(x, y, z);
            if(blockID == 0)
                return;

            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    for(int k = 0; k < 3; k++){
                        if(i == 1 && j == 1 && k == 1){
                            blockCache[1][1][1] = blockID;
                            continue;
                        }
                        blockCache[i][j][k] = this.getBlockID((x + i - 1), (y + j - 1), (z + k - 1));
                    }
                }
            }


            // add faces
            final BlockModel blockModel = BlockModelRegistry.getModel(blockID);

            // none faces
            this.addFaces(x, y, z, blockModel, Directory.NONE);

            // east faces
            final int blockCacheEast = this.getCachedBlockID(Directory.EAST);
            if(blockCacheEast == 0){
                this.addFaces(x, y, z, blockModel, Directory.EAST);

            }else if(blockCacheEast == blockID){
                final BlockModel eastBlockModel = BlockModelRegistry.getModel(blockCacheEast);
                if(eastBlockModel.isDontOccludeSameBlock() && eastBlockModel.isOcclude(Directory.EAST))
                    this.addFaces(x, y, z, blockModel, Directory.EAST);
            }

            // west faces
            final int blockCacheWest = this.getCachedBlockID(Directory.WEST);
            if(blockCacheWest == 0){
                this.addFaces(x, y, z, blockModel, Directory.WEST);

            }else if(blockCacheWest == blockID){
                final BlockModel westBlockModel = BlockModelRegistry.getModel(blockCacheWest);
                if(westBlockModel.isDontOccludeSameBlock() && westBlockModel.isOcclude(Directory.WEST))
                    this.addFaces(x, y, z, blockModel, Directory.WEST);
            }

            // up faces
            final int blockCacheUp = this.getCachedBlockID(Directory.UP);
            if(blockCacheUp == 0){
                this.addFaces(x, y, z, blockModel, Directory.UP);

            }else if(blockCacheUp == blockID){
                final BlockModel upBlockModel = BlockModelRegistry.getModel(blockCacheUp);
                if(upBlockModel.isDontOccludeSameBlock() && upBlockModel.isOcclude(Directory.UP))
                    this.addFaces(x, y, z, blockModel, Directory.UP);
            }

            // down faces
            final int blockCacheDown = this.getCachedBlockID(Directory.DOWN);
            if(blockCacheDown == 0){
                this.addFaces(x, y, z, blockModel, Directory.DOWN);

            }else if(blockCacheDown == blockID){
                final BlockModel downBlockModel = BlockModelRegistry.getModel(blockCacheDown);
                if(downBlockModel.isDontOccludeSameBlock() && downBlockModel.isOcclude(Directory.DOWN))
                    this.addFaces(x, y, z, blockModel, Directory.DOWN);
            }

            // north faces
            final int blockCacheNorth = this.getCachedBlockID(Directory.NORTH);
            if(blockCacheNorth == 0){
                this.addFaces(x, y, z, blockModel, Directory.NORTH);

            }else if(blockCacheNorth == blockID){
                final BlockModel northBlockModel = BlockModelRegistry.getModel(blockCacheNorth);
                if(northBlockModel.isDontOccludeSameBlock() && northBlockModel.isOcclude(Directory.NORTH))
                    this.addFaces(x, y, z, blockModel, Directory.NORTH);
            }

            // south faces
            final int blockCacheSouth = this.getCachedBlockID(Directory.SOUTH);
            if(blockCacheSouth == 0){
                this.addFaces(x, y, z, blockModel, Directory.SOUTH);

            }else if(blockCacheSouth == blockID){
                final BlockModel southBlockModel = BlockModelRegistry.getModel(blockCacheSouth);
                if(southBlockModel.isDontOccludeSameBlock() && southBlockModel.isOcclude(Directory.SOUTH))
                    this.addFaces(x, y, z, blockModel, Directory.SOUTH);
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
