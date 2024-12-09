package generaloss.mc24.accountservice;

import generaloss.mc24.accountservice.network.Connection;
import generaloss.mc24.accountservice.network.RequestListener;
import generaloss.mc24.accountservice.network.ResponseCode;
import jpize.util.io.ExtDataInputStream;
import jpize.util.io.FastReader;
import jpize.util.res.Resource;

import java.io.IOException;
import java.util.UUID;

public class Main {

    private final SessionDispatcher sessionDispatcher;

    public Main() {
        // init
        Resource.external("./accounts/").mkdir();
        System.out.println("Enter 'help' to list commands.");
        // server
        this.sessionDispatcher = new SessionDispatcher();
        final RequestListener requestListener = new RequestListener(this);
        // command line
        final CommandLineController commandLineCtrl = new CommandLineController();
        final FastReader reader = new FastReader();
        // loop
        while(!commandLineCtrl.isCloseRequest() && !Thread.interrupted()) {
            final String[] command = reader.nextLine().split(" ");
            commandLineCtrl.evalCommand(command);
        }
        // close
        reader.close();
        requestListener.close();
    }


    public void createAccount(Connection connection, ExtDataInputStream stream) throws IOException, IllegalArgumentException {
        // (nickname, password) -> (bool)
        Account.create(stream.readStringUTF(), stream.readStringUTF());
        connection.sendResponse(ResponseCode.NO_ERROR, "Account created successfully.");
    }

    public void deleteAccount(Connection connection, ExtDataInputStream stream) throws IOException, IllegalArgumentException {
        // (nickname, password) -> (bool)
        final String nickname = stream.readStringUTF();
        final String password = stream.readStringUTF();

        final Account account = Account.load(nickname);
        if(!account.getPassword().equals(password))
            throw new IllegalArgumentException("Wrong password.");

        Account.delete(account.getNickname());
        connection.sendResponse(ResponseCode.NO_ERROR, "Account deleted successfully.");
    }

    public void logInAccount(Connection connection, ExtDataInputStream stream) throws IOException, IllegalArgumentException {
        // (nickname, password) -> (sessionID)
        final String nickname = stream.readStringUTF();
        final String password = stream.readStringUTF();

        final Account account = Account.load(nickname);
        if(!account.getPassword().equals(password))
            throw new IllegalArgumentException("Wrong password.");

        final UUID sessionID = sessionDispatcher.logIn(account);
        connection.sendResponse(ResponseCode.NO_ERROR, sessionID);
    }

    public void logOutAccount(Connection connection, ExtDataInputStream stream) throws IOException, IllegalArgumentException {
        // (sessionID) -> (bool)
        final UUID sessionID = stream.readUUID();
        sessionDispatcher.logOut(sessionID);
        connection.sendResponse(ResponseCode.NO_ERROR, "Logged out successfully.");
    }

    public void hasSession(Connection connection, ExtDataInputStream stream) throws IOException, IllegalArgumentException {
        // (sessionID) -> (bool)
        final UUID sessionID = stream.readUUID();
        final Account account = sessionDispatcher.getSessionAccount(sessionID);
        connection.sendResponse(ResponseCode.NO_ERROR, account != null);
    }

    public void getSessionInfo(Connection connection, ExtDataInputStream inStream) throws IOException, IllegalArgumentException {
        // (sessionID) -> (nickname, creation_date)
        final UUID sessionID = inStream.readUUID();
        final Account account = sessionDispatcher.getSessionAccount(sessionID);
        if(account == null)
            throw new IllegalArgumentException("Session does not exist.");

        connection.sendResponse(ResponseCode.NO_ERROR, outStream -> {
            outStream.writeStringUTF(account.getNickname());
            outStream.writeStringUTF(account.getCreationDate());
        });
    }


    public static void main(String[] args) {
        new Main();
    }

}