package generaloss.mc24.server;

import jpize.util.math.vector.Vec3i;

public enum Facing {

    WEST  (1, new Vec3i(-1,  0,  0)), // 0
    EAST  (0, new Vec3i(+1,  0,  0)), // 1
    SOUTH (3, new Vec3i( 0,  0, -1)), // 2
    NORTH (2, new Vec3i( 0,  0, +1)); // 3

    private final int oppositeIndex;
    private final Vec3i normal;

    Facing(int oppositeIndex, Vec3i normal) {
        this.oppositeIndex = oppositeIndex;
        this.normal = normal;
    }

    public Facing opposite() {
        return values()[oppositeIndex];
    }

    public Vec3i getNormal() {
        return normal;
    }

    public int getX() {
        return normal.x;
    }

    public int getZ() {
        return normal.z;
    }


    public String getName() {
        return this.toString();
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }


    public static Facing byName(String name) {
        return valueOf(name.toUpperCase());
    }

    public static Facing byAngle(float yaw) {
        if(yaw >= -45 && yaw <  45) return SOUTH;
        if(yaw >=  45 && yaw < 135) return WEST;
        if(yaw < -45 && yaw >= -135) return EAST;
        return NORTH;
    }

}
