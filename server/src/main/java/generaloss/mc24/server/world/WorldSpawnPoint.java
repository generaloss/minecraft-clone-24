package generaloss.mc24.server.world;

import generaloss.mc24.server.chunkload.IChunkLoadEntry;
import jpize.util.math.vector.Vec3i;

public class WorldSpawnPoint implements IChunkLoadEntry {

    private final Vec3i point;
    private int distance;

    public WorldSpawnPoint() {
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
