package generaloss.mc24.server;

import generaloss.mc24.server.registry.Registries;
import jpize.util.net.tcp.TcpServer;
import jpize.util.res.Resource;
import jpize.util.time.Tickable;

public class Server implements Tickable {

    private int port;
    private final ServerConnections connections;
    private final TcpServer tcpServer;
    private final Registries registries;

    public Server(Registries registries) {
        System.out.println("Creating server");
        this.connections = new ServerConnections(this);
        this.tcpServer = new TcpServer()
            .setOnConnect(connections::onConnect)
            .setOnDisconnect(connections::onDisconnect)
            .setOnReceive(connections::onReceive);
        this.registries = registries;
    }

    public int getPort() {
        return port;
    }

    public TcpServer tcpServer() {
        return tcpServer;
    }

    public Registries registries() {
        return registries;
    }


    public void init() {
        System.out.println("Initializing server");
        this.loadBlocks();
    }
    
    private void loadBlocks() {
        for(Resource blockRes : registries.getDefaultPack().get("blocks/").listRes())
            registries.registerBlock(blockRes.path());
    }

    public void run(int port) {
        this.port = port;
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
