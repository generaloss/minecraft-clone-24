package generaloss.mc24.server.entity.player;

import generaloss.mc24.server.entity.AbstractEntity;
import jpize.util.math.EulerAngles;

import java.util.UUID;

public class AbstractPlayer extends AbstractEntity {

    public static final String ENTITY_TYPE_ID = "player";

    private final EulerAngles rotation;

    public AbstractPlayer(UUID uuid) {
        super(uuid, ENTITY_TYPE_ID);
        this.rotation = new EulerAngles();
    }

    public EulerAngles rotation() {
        return rotation;
    }

}
