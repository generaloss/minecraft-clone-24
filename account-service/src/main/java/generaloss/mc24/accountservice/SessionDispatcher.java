package generaloss.mc24.accountservice;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionDispatcher {

    private final Map<Account, UUID> sessionIDs;
    private final Map<UUID, Account> sessionAccounts;

    public SessionDispatcher() {
        this.sessionIDs = new ConcurrentHashMap<>();
        this.sessionAccounts = new ConcurrentHashMap<>();
    }

    public UUID logIn(Account account) {
        if(sessionIDs.containsKey(account))
            return sessionIDs.get(account);

        final UUID sessionID = UUID.randomUUID();
        sessionIDs.put(account, sessionID);
        sessionAccounts.put(sessionID, account);
        return sessionID;
    }

    public Account logOut(UUID sessionID) {
        final Account account = sessionAccounts.remove(sessionID);
        sessionIDs.remove(account);
        return account;
    }

    public Account getSessionAccount(UUID sessionID) {
        return sessionAccounts.get(sessionID);
    }

    public UUID getSessionID(Account account) {
        return sessionIDs.get(account);
    }

}
