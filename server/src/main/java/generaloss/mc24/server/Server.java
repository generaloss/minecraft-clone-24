package generaloss.mc24.server;

import generaloss.mc24.server.network.connection.NetServer;
import generaloss.mc24.server.player.PlayerList;
import generaloss.mc24.server.registry.ServerRegistries;
import generaloss.mc24.server.resourcepack.ResourcePackManager;
import generaloss.mc24.server.world.ServerWorld;
import generaloss.mc24.server.world.WorldHolder;
import generaloss.mc24.server.worldgen.ChunkGenerator01;
import jpize.util.res.Resource;
import jpize.util.res.ZipResource;
import jpize.util.time.TickGenerator;
import jpize.util.time.Tickable;

import java.util.Arrays;
import java.util.Comparator;

public class Server implements Tickable {

    private final ResourcePackManager resourcePackManager;
    private final NetServer net;
    private final ServerPropertiesHolder properties;
    private final WorldHolder worldHolder;
    private final PlayerList players;
    private Thread thread;

    public Server(ResourcePackManager resourcePackManager, boolean dedicated) {
        this.resourcePackManager = resourcePackManager;
        ServerRegistries.init(resourcePackManager);
        this.properties = new ServerPropertiesHolder();
        this.properties.set("dedicated", dedicated);
        this.net = new NetServer(this);
        this.worldHolder = new WorldHolder();
        this.players = new PlayerList(this);
    }

    public ResourcePackManager resourcePackManager() {
        return resourcePackManager;
    }

    public ServerPropertiesHolder properties() {
        return properties;
    }

    public NetServer net() {
        return net;
    }

    public WorldHolder worldHolder() {
        return worldHolder;
    }

    public PlayerList players() {
        return players;
    }


    public boolean isClosed() {
        return net.tcpServer().isClosed();
    }


    public void init() {
        // load blocks
        final ZipResource[] blockResources = resourcePackManager.getCorePack().getResource("blocks/").listResources();
        Arrays.sort(blockResources, Comparator.comparingInt(resource -> resource.path().hashCode()));
        for(Resource blockRes: blockResources)
            ServerRegistries.BLOCK.register(blockRes.path());

        // load all resources
        if(properties.getBool("dedicated"))
            ServerRegistries.loadResources();

        System.out.println("[INFO]: Loaded " + ServerRegistries.BLOCK.size() + " blocks");
        System.out.println("[INFO]: Created " + ServerRegistries.BLOCK_STATE.size() + " block states");

        if(properties.getBool("dedicated"))
            worldHolder.putWorld(new ServerWorld(this, "overworld", new ChunkGenerator01()));
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

    public void stop() {
        System.out.println("[INFO]: Server closed.");
        net.tcpServer().close();
        if(properties.getBool("dedicated"))
            thread.interrupt();
    }

    private void startServerThread() {
        if(properties.getBool("dedicated")){
            thread = new Thread(this::startLoop);
            thread.setDaemon(true);
            thread.start();
        }else{
            thread = Thread.currentThread();
            this.startLoop();
        }
    }

    private void startLoop() {
        final TickGenerator tickGenerator = new TickGenerator(20);
        tickGenerator.start(this);
    }

    @Override
    public void tick() {
        for(ServerWorld world: worldHolder.getWorlds())
            world.tick();
    }

}
