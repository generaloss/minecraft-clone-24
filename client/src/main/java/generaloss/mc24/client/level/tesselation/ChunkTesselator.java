package generaloss.mc24.client.level.tesselation;

import generaloss.mc24.client.level.LevelChunk;
import jpize.util.array.FloatList;
import jpize.util.math.Mathc;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class ChunkTesselator {

    public record Task(LevelChunk chunk, Consumer<ChunkMesh> onComplete) { }

    private final ChunkMeshCache meshCache;
    private final Queue<Task> taskQueue;
    private final FloatList vertexDataList;
    private final LevelChunk[][] chunkMatrix;

    public ChunkTesselator() {
        this.taskQueue = new LinkedList<>();
        this.meshCache = new ChunkMeshCache();
        this.vertexDataList = new FloatList();
        this.chunkMatrix = new LevelChunk[3][3];
    }

    public void tesselate(LevelChunk chunk, Consumer<ChunkMesh> onComplete) {
        if(chunk == null || onComplete == null)
            return;
        taskQueue.add(new Task(chunk, onComplete));
    }

    public void update() {
        while(!taskQueue.isEmpty()) {
            final Task task = taskQueue.poll();
            this.processTesselate(task.chunk, task.onComplete);
        }
    }

    private int getMatrixBlock(int x, int y, int z) {
        final int chunkX = 1 + Mathc.signum((float) x / LevelChunk.WIDTH);
        final int chunkZ = 1 + Mathc.signum((float) z / LevelChunk.WIDTH);
        final LevelChunk chunk = chunkMatrix[chunkX][chunkZ];
        if(chunk == null)
            return 0;

        final int norX = (x - (chunkX - 1) * LevelChunk.WIDTH);
        final int norZ = (z - (chunkZ - 1) * LevelChunk.WIDTH);
        return chunk.getBlock(norX, y, norZ);
    }

    private void processTesselate(LevelChunk chunk, Consumer<ChunkMesh> onComlete) {
        // set neighbor chunks
        chunkMatrix[0][0] = null;
        chunkMatrix[1][0] = null;
        chunkMatrix[2][0] = null;
        chunkMatrix[0][1] = null;
        chunkMatrix[1][1] = chunk;
        chunkMatrix[2][1] = null;
        chunkMatrix[0][2] = null;
        chunkMatrix[1][2] = null;
        chunkMatrix[2][2] = null;

        chunk.forEachBlock((x, y, z) -> {
            final int blockID = chunk.getBlock(x, y, z);
            if(blockID == 0)
                return;

            final int neighborNxID = this.getMatrixBlock(x - 1, y, z);
            if(neighborNxID == 0){
                vertexDataList.add(
                    x - 1, y + 1, z    ,
                    x - 1, y    , z    ,
                    x - 1, y    , z + 1,
                    x - 1, y + 1, z + 1
                );
            }
        });

        final ChunkMesh mesh = meshCache.getFreeOrCreate();
        mesh.setData(vertexDataList);
        vertexDataList.clear();

        onComlete.accept(mesh);
    }

}
