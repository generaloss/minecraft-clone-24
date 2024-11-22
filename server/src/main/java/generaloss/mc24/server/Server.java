package generaloss.mc24.server;

import generaloss.mc24.server.resource.ResourceDispatcher;
import jpize.util.net.tcp.TcpServer;
import jpize.util.res.ExternalResource;
import jpize.util.res.Resource;
import jpize.util.time.Tickable;

public class Server implements Tickable {

    private int port;
    private final ResourceDispatcher resources;
    private final ServerConnections connections;
    private final TcpServer tcpServer;

    public Server() {
        System.out.println("Initializing server");
        this.resources = new ResourceDispatcher();
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

    public ResourceDispatcher resources() {
        return resources;
    }

    public TcpServer getTcpServer() {
        return tcpServer;
    }


    private void loadResources() {
        // load block models
        final String blockModelsPath = "/blocks/";
        final ExternalResource defaultBlockModelsRes = Resource.external(resources.getDefaultDirectory() + blockModelsPath);
        System.out.println("Loading " + defaultBlockModelsRes.list().length + " blocks..");
        for(ExternalResource blockModelRes: defaultBlockModelsRes.listRes())
            resources.registerBlock(blockModelRes.simpleName(), blockModelsPath + blockModelRes.name());
        // reload
        resources.reloadAll();
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
