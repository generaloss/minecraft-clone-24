package generaloss.mc24.server.common;

import jpize.util.math.vector.Vec3i;

@FunctionalInterface
public interface FacingConsumer {

    void accept(int x, int z);

    static void forEach(int x, int z, FacingConsumer consumer) {
        for(Facing facing: Facing.values()){
            final Vec3i normal = facing.getNormal();

            final int neighborX = (x + normal.x);
            final int neighborZ = (z + normal.z);

            consumer.accept(neighborX, neighborZ);
        }
    }

}
