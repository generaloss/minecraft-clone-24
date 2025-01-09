package generaloss.mc24.server.network;

import generaloss.mc24.accountservice.network.Request;
import generaloss.mc24.accountservice.network.Response;
import generaloss.mc24.server.SharedConstants;

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

    public void logout(String password) {
        final Response resp = Request.sendLogout(SharedConstants.ACCOUNTS_HOST, username, password);
        System.out.println("[INFO]: Logout: " + resp.readBoolean());
    }

}
