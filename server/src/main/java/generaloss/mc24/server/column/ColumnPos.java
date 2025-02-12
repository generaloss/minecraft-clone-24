package generaloss.mc24.server.column;

import generaloss.mc24.server.chunk.Chunk;

import java.util.Objects;

public class ColumnPos {

    private final int x, z;
    private final long packed;

    public ColumnPos(int x, int z) {
        this.x = x;
        this.z = z;
        this.packed = ColumnPos.pack(x, z);
    }

    public ColumnPos(long packed) {
        // Long 64bit => [X 32bit, Z 32bit]
        this.x = (int) (packed      );
        this.z = (int) (packed >> 32);
        this.packed = packed;
    }

    public long getPacked() {
        return packed;
    }


    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }


    public int getBlockX() {
        return x * Chunk.SIZE;
    }

    public int getBlockZ() {
        return z * Chunk.SIZE;
    }


    public ColumnPos getNeighbor(int dx, int dz) {
        return new ColumnPos(x + dx, z + dz);
    }

    public long getNeighborPacked(int dx, int dz) {
        return ColumnPos.pack(x + dx,  z + dz);
    }


    @Override
    public String toString() {
        return x + ", " + z;
    }

    @Override
    public boolean equals(Object object) {
        if(object == null || getClass() != object.getClass())
            return false;
        final ColumnPos chunkPos = (ColumnPos) object;
        return (x == chunkPos.x && z == chunkPos.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }


    public static long pack(int x, int z) {
        // [X 32bit, Z 32bit] => Long 64bit
        return (x & 0xFFFFFFFFL | (z & 0xFFFFFFFFL) << 32);
    }

    public static ColumnPos unpack(long packed) {
        return new ColumnPos(packed);
    }

}
