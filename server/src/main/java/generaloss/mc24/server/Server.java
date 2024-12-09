package generaloss.mc24.server;

import generaloss.mc24.server.network.connection.NetServer;
import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.resourcepack.ResourceBlock;
import generaloss.mc24.server.world.WorldHolder;
import jpize.util.res.Resource;
import jpize.util.time.Tickable;

import java.util.Map;

public class Server implements Tickable {

    private final Registries registries;
    private final NetServer net;
    private final ServerPropertiesHolder properties;
    private final WorldHolder worldHolder;

    public Server(Registries registries, boolean dedicated) {
        this.registries = registries;
        this.properties = new ServerPropertiesHolder();
        this.properties.set("dedicated", dedicated);
        this.net = new NetServer(this);
        this.worldHolder = new WorldHolder();
    }

    public NetServer net() {
        return net;
    }

    public Registries registries() {
        return registries;
    }

    public ServerPropertiesHolder properties() {
        return properties;
    }

    public WorldHolder worldHolder() {
        return worldHolder;
    }


    public boolean isClosed() {
        return net.tcpServer().isClosed();
    }


    public void init() {
        System.out.println("[INFO]: Init server");
        // load blocks
        for(Resource blockRes: registries.getDefaultPack().getResource("blocks/").listResources())
            registries.BLOCKS.register(blockRes.path());
        // create block states
        for(ResourceBlock blockRes: registries.BLOCKS.getResourcesToLoad())
            blockRes.getObject().createBlockStates(Map.of(), registries);
        // load all resources
        if(properties.getBool("dedicated"))
            registries.loadResources();

        System.out.println("[INFO]: Loaded " + registries.BLOCKS.size() + " blocks");
        System.out.println("[INFO]: Created " + registries.BLOCK_STATES.size() + " block states");
    }
    
    public void run(int port) {
        properties.set("port", port);
        try{
            net.tcpServer().run(port);
            System.out.println("[INFO]: Server running on port " + port);
            this.startServerThread();
        }catch(Exception e){
            System.err.println("[ERROR]: Error running server: " + e.getMessage());
        }
    }

    private void startServerThread() {

    }

    public void stop() {
        System.out.println("[INFO]: Server closed.");
        net.tcpServer().close();
    }

    @Override
    public void tick() {

    }

}
