package generaloss.mc24.server.player;

import jpize.util.math.EulerAngles;
import jpize.util.math.vector.Vec3f;

public class AbstractPlayer {

    private final Vec3f position;
    private final EulerAngles rotation;

    public AbstractPlayer() {
        this.position = new Vec3f();
        this.rotation = new EulerAngles();
    }

    public Vec3f getPosition() {
        return position;
    }

    public EulerAngles getRotation() {
        return rotation;
    }

}
