import generaloss.mc24.accountservice.network.Response;
import generaloss.mc24.accountservice.network.Request;
import jpize.util.math.Maths;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ClientTest {

    public static void main(String[] args) {
        final String username = "user" + Maths.randomSeed(2);
        final String password = UUID.randomUUID().toString().substring(0, 8);
        createAccount(username, password);
        login(username, password);
        UUID uuid = login(username, password);
        hasSession(uuid);
        getSessionInfo(uuid);
        logout(uuid);
        hasSession(uuid);
        getSessionInfo(uuid);
        deleteAccount(username, password);
    }

    private static void hasSession(UUID uuid) {
        // send request
        final Response response = Request.sendHasSession("localhost", uuid);
        // print response
        response.readData(stream ->
            System.out.println(response.getCode() + ", " + stream.readBoolean()));
    }

    private static void getSessionInfo(UUID uuid) {
        // send request
        final Response response = Request.sendGetSessionInfo("localhost", uuid);
        // print response
        response.readData(stream ->
            System.out.println(response.getCode() + ", " + stream.readStringUTF() + ", " + stream.readStringUTF()));
    }

    private static void logout(UUID uuid) {
        // send request
        final Response response = Request.sendLogout("localhost", uuid);
        // print response

        response.readData(stream ->
            System.out.println(response.getCode() + ", " + stream.readStringUTF()));
    }

    private static UUID login(String nickname, String password) {
        // send request
        final Response response = Request.sendLogin("localhost", nickname, password);
        // print response
        final AtomicReference<UUID> uuid = new AtomicReference<>();
        response.readData(stream -> {
            uuid.set(stream.readUUID());
            System.out.println(response.getCode() + ", " + uuid.get());
        });
        return uuid.get();
    }

    private static void createAccount(String nickname, String password) {
        // send request
        final Response response = Request.sendCreateAccount("localhost", nickname, password);
        // print response
        response.readData(stream -> System.out.println(response.getCode() + ", " + stream.readStringUTF()));
    }

    private static void deleteAccount(String nickname, String password) {
        // send request
        final Response response = Request.sendDeleteAccount("localhost", nickname, password);
        // print response
        response.readData(stream -> System.out.println(response.getCode() + ", " + stream.readStringUTF()));
    }

}
