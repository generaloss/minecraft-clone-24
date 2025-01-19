package generaloss.mc24.server.player;

import jpize.util.math.EulerAngles;
import jpize.util.math.vector.Vec3f;

import java.util.UUID;

public class AbstractPlayer {

    private final UUID uuid;
    private final Vec3f position;
    private final EulerAngles rotation;

    public AbstractPlayer(UUID uuid) {
        this.uuid = uuid;
        this.position = new Vec3f();
        this.rotation = new EulerAngles();
    }

    public UUID getUUID() {
        return uuid;
    }

    public Vec3f position() {
        return position;
    }

    public EulerAngles rotation() {
        return rotation;
    }

}
