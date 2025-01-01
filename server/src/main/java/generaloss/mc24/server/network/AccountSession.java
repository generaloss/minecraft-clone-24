package generaloss.mc24.server.network;

import java.util.UUID;

public class AccountSession {

    private final UUID ID;
    private final String username;

    public AccountSession(UUID ID, String username) {
        this.ID = ID;
        this.username = username;
    }

    public UUID getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

}
