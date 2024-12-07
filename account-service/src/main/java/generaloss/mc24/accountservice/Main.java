package generaloss.mc24.accountservice;

import generaloss.mc24.accountservice.packet.Packet2CPublicKey;
import generaloss.mc24.accountservice.packet.Packet2SConnectionKey;
import jpize.util.io.FastReader;
import jpize.util.net.tcp.TcpConnection;
import jpize.util.net.tcp.TcpServer;
import jpize.util.net.tcp.packet.PacketDispatcher;
import jpize.util.res.ExternalResource;
import jpize.util.res.Resource;
import jpize.util.security.KeyRSA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final Map<TcpConnection, AccountServiceServerConnection> connections = new HashMap<>();
    private static final KeyRSA key = new KeyRSA(1024);
    private static final PacketDispatcher packetDispatcher = new PacketDispatcher().register(
        Packet2SConnectionKey.class
    );

    public static void main(String[] args) {
        Resource.external("./accounts/").mkdir();

        final TcpServer tcpServer = new TcpServer()
            .setOnConnect(Main::onConnect)
            .setOnDisconnect(Main::onDisconnect)
            .setOnReceive(Main::onReceive)
            .run(8888);

        System.out.println("Enter 'help' to list commands.");
        final FastReader cliReader = new FastReader();
        while(true) {
            final String[] command = cliReader.nextLine().split(" ");
            switch (command[0]) {
                case "help" -> {
                    if(command.length > 1 && command[1].equals("account")){
                        System.out.println("Command 'account []'");
                    }else{
                        System.out.println("Commands list:");
                        System.out.println("  help [command]");
                        System.out.println("  account [option{create, remove, list, info}] [nickname]");
                        System.out.println("  quit");
                    }
                }
                case "account" -> {
                    if(command.length < 2)
                        break;
                    final String option = command[1];
                    switch (option) {
                        case "create" -> {
                            if(command.length != 3)
                                break;
                            final String nickname = command[2];

                            Resource.external("./accounts/" + nickname).mkdir();
                            System.out.println("Created account '" + nickname + "'");
                        }
                        case "remove" -> {
                            if(command.length != 3)
                                break;
                            final String nickname = command[2];

                            final ExternalResource directory = Resource.external("./accounts/" + nickname);
                            Arrays.stream(directory.listResources())
                                .forEach(ExternalResource::delete);
                            directory.delete();
                            System.out.println("Deleted account '" + nickname + "'");
                        }
                        case "list" -> {
                            System.out.println("Account list: [...]");
                        }
                        case "info" -> {
                            if(command.length != 3)
                                break;
                            final String nickname = command[2];
                            System.out.println("Account info:");
                            System.out.println("  nickname: " + nickname);
                            System.out.println("  ...");
                        }
                    }
                }
                case "quit" -> {
                    cliReader.close();
                    tcpServer.close();
                    return;
                }
                default -> System.out.println("Unknown command: " + command[0]);
            }
        }
    }

    private static void onConnect(TcpConnection tcpConnection) {
        connections.put(tcpConnection, new AccountServiceServerConnection(tcpConnection, key));
        tcpConnection.send(new Packet2CPublicKey(key.getPublic()));
    }

    private static void onDisconnect(TcpConnection tcpConnection) {
        connections.remove(tcpConnection);
    }

    private static void onReceive(TcpConnection tcpConnection, byte[] bytes) {
        final AccountServiceServerConnection connection = connections.get(tcpConnection);
        packetDispatcher.readPacket(bytes, connection);
        packetDispatcher.handlePackets();
    }

}