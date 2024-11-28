package generaloss.mc24.server;

import jpize.util.math.vector.Vec3i;

public enum Directory {

    WEST  (1, new Vec3i(-1,  0,  0)), // 0
    EAST  (0, new Vec3i(+1,  0,  0)), // 1
    DOWN  (3, new Vec3i( 0, -1,  0)), // 2
    UP    (2, new Vec3i( 0, +1,  0)), // 3
    SOUTH (5, new Vec3i( 0,  0, -1)), // 4
    NORTH (4, new Vec3i( 0,  0, +1)), // 5

    NONE  (6, new Vec3i( 0,  0,  0)); // 6

    private final int oppositeIndex;
    private final Vec3i normal;

    Directory(int oppositeIndex, Vec3i normal) {
        this.oppositeIndex = oppositeIndex;
        this.normal = normal;
    }

    public Directory opposite() {
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


    public static Directory byVector(double x, double y, double z) {
        if(x != 0) return (x < 0) ? WEST : EAST;
        if(y != 0) return (y < 0) ? DOWN : UP;
        if(z != 0) return (z < 0) ? SOUTH : NORTH;
        return NONE;
    }

}
