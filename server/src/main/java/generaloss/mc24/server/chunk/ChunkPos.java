package generaloss.mc24.server.chunk;

import jpize.util.math.Frustum;

import java.util.Objects;

public class ChunkPos {

    public static final long X_MASK = 0xFFFFFFL; // max 24 bit value
    public static final long Y_MASK = 0xFFFFL;   // max 16 bit value
    public static final long Z_MASK = 0xFFFFFFL; // max 24 bit value
    public static final int MAX_X = (int) X_MASK / 2;
    public static final int MAX_Y = (int) Y_MASK / 2;
    public static final int MAX_Z = (int) Z_MASK / 2;
    public static final int MIN_X = (int) -X_MASK / 2 - 1;
    public static final int MIN_Y = (int) -Y_MASK / 2 - 1;
    public static final int MIN_Z = (int) -Z_MASK / 2 - 1;

    private final int x, y, z;

    public ChunkPos(int x, int y, int z) {
        if(x > MAX_X || y > MAX_Y || z > MAX_Z || x < MIN_X || y < MIN_Y || z < MIN_Z)
            throw new IllegalStateException("Too big chunk coordinates.");

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ChunkPos(long packed) {
        // Long 64bit => [X 24bit, Y 16bit, Z 24bit]
        this.x = (int) (((packed      )) & X_MASK) + MIN_X;
        this.y = (int) (((packed >> 24)) & Y_MASK) + MIN_Y;
        this.z = (int) (((packed >> 40)) & Z_MASK) + MIN_Z;
    }

    public long pack() {
        return pack(this);
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
        return frustum.isAABoxIn(x, y, z, x + Chunk.SIZE, y + Chunk.SIZE, z + Chunk.SIZE);
    }

    public ChunkPos getNeighbor(int dx, int dy, int dz) {
        return new ChunkPos(x + dx, y + dy, z + dz);
    }

    public long getNeighborPacked(int dx, int dy, int dz) {
        return ChunkPos.pack(x + dx, y + dy, z + dz);
    }


    @Override
    public String toString() {
        return x + ", " + y + ", " + z;
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


    public static long pack(int x, int y, int z) {
        // [X 24bit, Y 16bit, Z 24bit] => Long 64bit
        return ((x - MIN_X) & X_MASK) | ((y - MIN_Y) & Y_MASK) << 24 | ((z - MIN_Z) & Z_MASK) << 40;
    }

    public static long pack(ChunkPos position) {
        return pack(position.x, position.y, position.z);
    }

    public static ChunkPos unpack(long packed) {
        return new ChunkPos(packed);
    }

}
