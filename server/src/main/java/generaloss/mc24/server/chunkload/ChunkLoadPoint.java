package generaloss.mc24.server.chunkload;

import jpize.util.math.vector.Vec3i;

public class ChunkLoadPoint implements IChunkLoadEntry {

    private final Vec3i point;
    private int distance;

    public ChunkLoadPoint() {
        this.point = new Vec3i();
    }

    public Vec3i point() {
        return point;
    }

    @Override
    public int getChunkX() {
        return point.x;
    }

    @Override
    public int getChunkY() {
        return point.y;
    }

    @Override
    public int getChunkZ() {
        return point.z;
    }

    @Override
    public int getChunkLoadDistance() {
        return distance;
    }

    public void setChunkLoadDistance(int distance) {
        this.distance = distance;
    }

}
