package generaloss.mc24.server;

import generaloss.mc24.server.resource.ResourcesRegistry;
import jpize.util.net.tcp.TcpServer;
import jpize.util.res.ExternalResource;
import jpize.util.res.Resource;
import jpize.util.time.Tickable;

public class Server implements Tickable {

    private int port;
    private final ResourcesRegistry resources;
    private final ServerConnections connections;
    private final TcpServer tcpServer;

    public Server(ResourcesRegistry resources) {
        System.out.println("Initializing server");
        this.resources = resources;
        this.loadResources();
        this.connections = new ServerConnections(this);
        this.tcpServer = new TcpServer()
            .setOnConnect(connections::onConnect)
            .setOnDisconnect(connections::onDisconnect)
            .setOnReceive(connections::onReceive);
    }

    public int getPort() {
        return port;
    }

    public ResourcesRegistry resources() {
        return resources;
    }

    public TcpServer getTcpServer() {
        return tcpServer;
    }


    private void loadResources() {
        // load blocks
        final String blocksPath = "/behaviours/blocks/";
        final ExternalResource blocksRes = Resource.external(resources.getDefaultDirectory() + blocksPath);
        System.out.println("Loading " + blocksRes.list().length + " blocks..");
        for(ExternalResource blockRes: blocksRes.listRes())
            resources.registerBlock(blockRes.simpleName(), blocksPath + blockRes.name());
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
