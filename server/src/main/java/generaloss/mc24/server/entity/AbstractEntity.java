package generaloss.mc24.server.entity;

import jpize.util.math.vector.Vec3f;

import java.util.UUID;

public class AbstractEntity {

    private final UUID uuid;
    private final String typeID;
    private final Vec3f position;
    private String displayName;

    public AbstractEntity(UUID uuid, String typeID) {
        this.uuid = uuid;
        this.typeID = typeID;
        this.position = new Vec3f();
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getTypeID() {
        return typeID;
    }

    public Vec3f position() {
        return position;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
