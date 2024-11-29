package generaloss.mc24.server;

import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.world.WorldHolder;
import jpize.util.net.tcp.TcpServer;
import jpize.util.res.Resource;
import jpize.util.time.Tickable;

public class Server implements Tickable {

    private int port;
    private final ServerConnections connections;
    private final TcpServer tcpServer;
    private final Registries registries;
    private final WorldHolder worldHolder;

    public Server(Registries registries) {
        System.out.println("Creating server");
        this.connections = new ServerConnections(this);
        this.tcpServer = new TcpServer()
            .setOnConnect(connections::onConnect)
            .setOnDisconnect(connections::onDisconnect)
            .setOnReceive(connections::onReceive);
        this.registries = registries;
        this.worldHolder = new WorldHolder();
    }

    public int getPort() {
        return port;
    }

    public ServerConnections connections() {
        return connections;
    }

    public TcpServer tcpServer() {
        return tcpServer;
    }

    public Registries registries() {
        return registries;
    }

    public WorldHolder worldHolder() {
        return worldHolder;
    }


    public boolean isClosed() {
        return tcpServer.isClosed();
    }


    public void init() {
        System.out.println("Initializing server");
        for(Resource blockRes: registries.getDefaultPack().getResource("blocks/").listRes())
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

    public void stop() {
        System.out.println("Server closed.");
        tcpServer.close();
    }

    @Override
    public void tick() {

    }

}
