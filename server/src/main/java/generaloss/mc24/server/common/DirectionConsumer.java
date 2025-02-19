package generaloss.mc24.server.common;

import jpize.util.math.vector.Vec3i;

@FunctionalInterface
public interface DirectionConsumer {

    void accept(int x, int y, int z);

    static void forEach(int x, int y, int z, DirectionConsumer consumer) {
        for(int i = 0; i < 6; i++){
            final Vec3i normal = Direction.values()[i].getNormal();

            final int neighborX = (x + normal.x);
            final int neighborY = (y + normal.y);
            final int neighborZ = (z + normal.z);

            consumer.accept(neighborX, neighborY, neighborZ);
        }
    }

    static void forEach(int x, int y, int z, boolean downLock, boolean upLock, DirectionConsumer consumer) {
        for(int i = 0; i < 6; i++){
            final Vec3i normal = Direction.values()[i].getNormal();

            if(downLock && normal.getY() == -1){
                continue;
            }else if(upLock && normal.getY() == 1){
                continue;
            }

            final int neighborX = (x + normal.x);
            final int neighborY = (y + normal.y);
            final int neighborZ = (z + normal.z);

            consumer.accept(neighborX, neighborY, neighborZ);
        }
    }

}
