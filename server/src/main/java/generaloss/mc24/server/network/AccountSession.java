package generaloss.mc24.server.network;

import java.util.UUID;

public class AccountSession {

    private UUID ID;
    private String nickname;

    public void set(UUID ID, String nickname) {
        this.ID = ID;
        this.nickname = nickname;
    }

    public UUID getID() {
        return ID;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isValid() {
        return ID != null;
    }

}
