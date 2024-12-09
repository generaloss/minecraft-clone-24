package generaloss.mc24.server.network;

import java.util.UUID;

public class NetSession {

    private final UUID ID;
    private final String nickname;

    public NetSession(UUID ID, String nickname) {
        this.ID = ID;
        this.nickname = nickname;
    }

    public UUID getID() {
        return ID;
    }

    public String getNickname() {
        return nickname;
    }

}
