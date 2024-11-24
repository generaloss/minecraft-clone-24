package generaloss.mc24.server;

import jpize.util.math.vector.Vec3i;

public enum Directory {

    NONE  ( 0,  0,  0), // 0
    WEST  (-1,  0,  0), // 1
    EAST  (+1,  0,  0), // 2
    DOWN  ( 0, -1,  0), // 3
    UP    ( 0, +1,  0), // 4
    SOUTH ( 0,  0, -1), // 5
    NORTH ( 0,  0, +1); // 6

    private final Vec3i vector;

    Directory(int x, int y, int z) {
        this.vector = new Vec3i(x, y, z);
    }


    public int getX() {
        return vector.x;
    }

    public int getY() {
        return vector.y;
    }

    public int getZ() {
        return vector.z;
    }

    public Directory opposite() {
        return byVector(-vector.x, -vector.y, -vector.z);
    }


    public static Directory byVector(double x, double y, double z) {
        if(x != 0) return (x < 0) ? WEST : EAST;
        if(y != 0) return (y < 0) ? DOWN : UP;
        if(z != 0) return (z < 0) ? SOUTH : NORTH;
        return NONE;
    }

}
