package generaloss.mc24.server;

import jpize.util.net.tcp.TcpServer;
import jpize.util.time.Tickable;

public abstract class Server implements Tickable {

    private final int port;
    private final ServerConnections connections;
    private final TcpServer tcpServer;

    public Server(int port) {
        System.out.println("Initializing Server");
        this.port = port;
        this.connections = new ServerConnections(this);
        this.tcpServer = new TcpServer()
            .setOnConnect(connections::onConnect)
            .setOnDisconnect(connections::onDisconnect)
            .setOnReceive(connections::onReceive);
    }

    public int getPort() {
        return port;
    }

    public TcpServer getTcpServer() {
        return tcpServer;
    }


    public void run() {
        try{
            tcpServer.run(port);
            System.out.println("Server running on port " + port);
            this.startServerThread();
        }catch(Exception e){
            System.err.println("Error running server: " + e.getMessage());
        }
    }

    private void startServerThread() {

    }

    @Override
    public void tick() {

    }

}
