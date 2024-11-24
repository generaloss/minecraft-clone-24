package generaloss.mc24.server;

import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.registry.Registries;
import jpize.util.net.tcp.TcpServer;
import jpize.util.res.ExternalResource;
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
        // load blocks
        final String blocksPath = "/assets/behaviours/blocks/";
        final ExternalResource blocksRes = Resource.external(blocksPath);
        System.out.println("Loading " + blocksRes.list().length + " blocks..");
        
        for(ExternalResource blockRes: blocksRes.listRes()){
            Resource resource = Resource.external(blocksPath + blockRes.name());
            System.out.println(resource.name());
            registries.BLOCK.register(blockRes.simpleName(), new Block(resource.simpleName()));
        }
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
