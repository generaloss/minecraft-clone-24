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
    private final ChunkMeshCache meshCache;
    private final Queue<LevelChunk> taskQueue;
    private final FloatList verticesCache;
    private final LevelChunk[][][] chunkCache;
    private int blockCacheCenter, blockCacheWest, blockCacheEast,
        blockCacheDown, blockCacheUp, blockCacheSouth, blockCacheNorth;

    public ChunkTesselator(Main context, WorldLevel level) {
        this.context = context;
        this.level = level;
        this.taskQueue = new LinkedList<>();
        this.meshCache = new ChunkMeshCache();
        this.verticesCache = new FloatList();
        this.chunkCache = new LevelChunk[3][3][3];
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

            if(timer.getMillis() > Jpize.getDeltaTime() * 700)
                break;
        }
    }

    private LevelChunk getCachedChunk(int chunkX, int chunkY, int chunkZ) {
        return chunkCache[chunkX + 1][chunkY + 1][chunkZ + 1];
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
        for(BlockFace face: model.getFacesGroup(directory)){
            // add face
            final int beginDataIndex = verticesCache.size();
            verticesCache.add(face.getVertexDataArray());
            // correct position
            for(int beginVertexIndex: face.getIndices()){
                final int index = (beginDataIndex + beginVertexIndex);
                // position
                verticesCache.valAdd(index + 0, x);
                verticesCache.valAdd(index + 1, y);
                verticesCache.valAdd(index + 2, z);
                // texcoord
                final ResourceAtlas atlas = context.resources().get("block_atlas");
                final TextureRegion region = atlas.getRegion(face.getTextureID());
                if(region != null){
                    final float u = (region.u1() + region.getWidth()  * verticesCache.get(index + BlockVertex.TEXCOORD_OFFSET + 0));
                    final float v = (region.v1() + region.getHeight() * verticesCache.get(index + BlockVertex.TEXCOORD_OFFSET + 1));
                    verticesCache.set(index + BlockVertex.TEXCOORD_OFFSET + 0, u);
                    verticesCache.set(index + BlockVertex.TEXCOORD_OFFSET + 1, v);
                }
                // color
                final float grayscale = 1F;
                verticesCache.valMul(index + BlockVertex.COLOR_OFFSET + 0, grayscale);
                verticesCache.valMul(index + BlockVertex.COLOR_OFFSET + 1, grayscale);
                verticesCache.valMul(index + BlockVertex.COLOR_OFFSET + 2, grayscale);
                verticesCache.valMul(index + BlockVertex.COLOR_OFFSET + 3, 1);
            }
        }
    }

    private void processTesselate(LevelChunk chunk) {
        // cache neighbor chunks

        final ChunkPos chunkPos = chunk.getPosition();
        chunkCache[1][1][1] = chunk;

        for(int x = 0; x < 3; x++){
            for(int z = 0; z < 3; z++){
                for(int y = 0; y < 3; y++){
                    if(x == 1 && y == 1 && z == 1)
                        continue;

                    chunkCache[x][y][z] = level.getChunk(chunkPos.getNeighbor(x - 1, y - 1, z - 1));
                }
            }
        }


        chunk.forEachBlock((x, y, z) -> {
            blockCacheCenter = chunk.getBlock(x, y, z);
            if(blockCacheCenter == 0)
                return;

            // cache neighbor blocks
            blockCacheWest  = this.getBlockID(x - 1, y, z);
            blockCacheEast  = this.getBlockID(x + 1, y, z);
            blockCacheDown  = this.getBlockID(x, y - 1, z);
            blockCacheUp    = this.getBlockID(x, y + 1, z);
            blockCacheSouth = this.getBlockID(x, y, z - 1);
            blockCacheNorth = this.getBlockID(x, y, z + 1);

            final BlockModel blockModel = BlockModelRegistry.getModel(blockCacheCenter);

            // NONE
            this.addFaces(x, y, z, blockModel, Directory.NONE);

            // EAST
            if(blockCacheEast == 0){
                this.addFaces(x, y, z, blockModel, Directory.EAST);

            }else if(blockCacheEast == blockCacheCenter){
                final BlockModel eastBlockModel = BlockModelRegistry.getModel(blockCacheEast);
                if(eastBlockModel.isDontOccludeSameBlock() && eastBlockModel.isOcclude(Directory.EAST))
                    this.addFaces(x, y, z, blockModel, Directory.EAST);
            }

            // WEST
            if(blockCacheWest == 0){
                this.addFaces(x, y, z, blockModel, Directory.WEST);

            }else if(blockCacheWest == blockCacheCenter){
                final BlockModel westBlockModel = BlockModelRegistry.getModel(blockCacheWest);
                if(westBlockModel.isDontOccludeSameBlock() && westBlockModel.isOcclude(Directory.WEST))
                    this.addFaces(x, y, z, blockModel, Directory.WEST);
            }

            // UP
            if(blockCacheUp == 0){
                this.addFaces(x, y, z, blockModel, Directory.UP);

            }else if(blockCacheUp == blockCacheCenter){
                final BlockModel upBlockModel = BlockModelRegistry.getModel(blockCacheUp);
                if(upBlockModel.isDontOccludeSameBlock() && upBlockModel.isOcclude(Directory.UP))
                    this.addFaces(x, y, z, blockModel, Directory.UP);
            }

            // DOWN
            if(blockCacheDown == 0){
                this.addFaces(x, y, z, blockModel, Directory.DOWN);

            }else if(blockCacheDown == blockCacheCenter){
                final BlockModel downBlockModel = BlockModelRegistry.getModel(blockCacheDown);
                if(downBlockModel.isDontOccludeSameBlock() && downBlockModel.isOcclude(Directory.DOWN))
                    this.addFaces(x, y, z, blockModel, Directory.DOWN);
            }

            // NORTH
            if(blockCacheNorth == 0){
                this.addFaces(x, y, z, blockModel, Directory.NORTH);

            }else if(blockCacheNorth == blockCacheCenter){
                final BlockModel northBlockModel = BlockModelRegistry.getModel(blockCacheNorth);
                if(northBlockModel.isDontOccludeSameBlock() && northBlockModel.isOcclude(Directory.NORTH))
                    this.addFaces(x, y, z, blockModel, Directory.NORTH);
            }

            // SOUTH
            if(blockCacheSouth == 0){
                this.addFaces(x, y, z, blockModel, Directory.SOUTH);

            }else if(blockCacheSouth == blockCacheCenter){
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
