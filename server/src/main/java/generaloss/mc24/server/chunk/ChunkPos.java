package generaloss.mc24.server.chunk;

import jpize.util.math.matrix.Frustum;

import java.util.Objects;

public class ChunkPos {

    private final int x, y, z;

    public ChunkPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getBlockX() {
        return x * Chunk.SIZE;
    }

    public int getBlockY() {
        return y * Chunk.SIZE;
    }

    public int getBlockZ() {
        return z * Chunk.SIZE;
    }


    public boolean isVisible(Frustum frustum) {
        final int x = this.getBlockX();
        final int y = this.getBlockY();
        final int z = this.getBlockZ();
        return frustum.isBoxInFrustum(x, y, z, x + Chunk.SIZE, y + Chunk.SIZE, z + Chunk.SIZE);
    }

    public ChunkPos getNeighbor(int dx, int dy, int dz) {
        return new ChunkPos(x + dx, y + dy, z + dz);
    }


    @Override
    public boolean equals(Object object) {
        if(object == null || getClass() != object.getClass())
            return false;
        final ChunkPos chunkPos = (ChunkPos) object;
        return (x == chunkPos.x && y == chunkPos.y && z == chunkPos.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

}
