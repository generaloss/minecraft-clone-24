package generaloss.mc24.server;

import jpize.util.math.matrix.Matrix4f;
import jpize.util.math.vector.Vec3f;
import jpize.util.math.vector.Vec3i;

public enum Direction {

    WEST  (1, new Vec3i(-1,  0,  0), 0.65F), // 0
    EAST  (0, new Vec3i(+1,  0,  0), 0.75F), // 1
    DOWN  (3, new Vec3i( 0, -1,  0), 0.55F), // 2
    UP    (2, new Vec3i( 0, +1,  0), 1.00F), // 3
    SOUTH (5, new Vec3i( 0,  0, -1), 0.80F), // 4
    NORTH (4, new Vec3i( 0,  0, +1), 0.90F), // 5

    NONE  (6, new Vec3i( 0,  0,  0), 1.00F); // 6

    private final int oppositeIndex;
    private final Vec3i normal;
    private final float blockShade;

    Direction(int oppositeIndex, Vec3i normal, float blockShade) {
        this.oppositeIndex = oppositeIndex;
        this.normal = normal;
        this.blockShade = blockShade;
    }

    public Direction opposite() {
        return values()[oppositeIndex];
    }

    public Vec3i getNormal() {
        return normal;
    }

    public int getX() {
        return normal.x;
    }

    public int getY() {
        return normal.y;
    }

    public int getZ() {
        return normal.z;
    }


    public float getBlockShade() {
        return blockShade;
    }


    public Direction getRotated(Matrix4f matrix) {
        final Vec3i normalRotated = normal.copy().mulMat4(matrix);
        return byVector(normalRotated);
    }


    public boolean isWest() {
        return this == WEST;
    }

    public boolean isEast() {
        return this == EAST;
    }

    public boolean isDown() {
        return this == DOWN;
    }

    public boolean isUp() {
        return this == UP;
    }

    public boolean isSouth() {
        return this == SOUTH;
    }

    public boolean isNorth() {
        return this == NORTH;
    }

    public boolean isNone() {
        return this == NONE;
    }


    public static Direction byVector(double x, double y, double z) {
        if(x != 0) return (x < 0) ? WEST : EAST;
        if(y != 0) return (y < 0) ? DOWN : UP;
        if(z != 0) return (z < 0) ? SOUTH : NORTH;
        return NONE;
    }

    public static Direction byVector(Vec3i vector) {
        return byVector(vector.x, vector.y, vector.z);
    }

    public static Direction byVector(Vec3f vector) {
        return byVector(vector.x, vector.y, vector.z);
    }

}
