package generaloss.mc24.server;

import generaloss.mc24.server.network.connection.ServerNetwork;
import generaloss.mc24.server.player.PlayerList;
import generaloss.mc24.server.registry.ServerRegistries;
import generaloss.mc24.server.resources.pack.ResourcePackManager;
import generaloss.mc24.server.world.ServerWorld;
import generaloss.mc24.server.world.WorldHolder;
import generaloss.mc24.server.worldgen.ChunkGenerator2DNoise;
import jpize.util.res.Resource;
import jpize.util.res.ZipResource;
import jpize.util.time.TickGenerator;
import jpize.util.time.Tickable;

import java.util.Arrays;
import java.util.Comparator;

public class Server implements Tickable {

    private final ResourcePackManager resourcePackManager;
    private final ServerNetwork network;
    private final ServerPropertyHolder properties;
    private final boolean dedicated;
    private final WorldHolder worldHolder;
    private final PlayerList players;
    private Thread thread;

    public Server(ResourcePackManager resourcePackManager, boolean dedicated) {
        this.resourcePackManager = resourcePackManager;
        ServerRegistries.init(resourcePackManager);
        this.properties = new ServerPropertyHolder();
        this.dedicated = dedicated;
        this.network = new ServerNetwork(this);
        this.worldHolder = new WorldHolder();
        this.players = new PlayerList(this);
    }

    public ResourcePackManager resourcePackManager() {
        return resourcePackManager;
    }

    public ServerPropertyHolder properties() {
        return properties;
    }

    public ServerNetwork network() {
        return network;
    }

    public WorldHolder worldHolder() {
        return worldHolder;
    }

    public PlayerList players() {
        return players;
    }


    public boolean isClosed() {
        return network.tcpServer().isClosed();
    }


    public void init() {
        // load blocks
        final ZipResource[] blockResources = resourcePackManager.getCorePack().getResource("blocks/").listResources();
        Arrays.sort(blockResources, Comparator.comparingInt(resource -> resource.path().hashCode()));
        for(Resource blockRes: blockResources) {
            ServerRegistries.BLOCK.create(blockRes.simpleName(), blockRes.path());
        }

        System.out.println("[INFO]: Loaded " + ServerRegistries.BLOCK.size() + " blocks");
        System.out.println("[INFO]: Created " + ServerRegistries.BLOCK_STATE.size() + " block states");

        if(dedicated)
            worldHolder.putWorld(new ServerWorld(this, "overworld", new ChunkGenerator2DNoise()));
    }
    
    public void run(int port) {
        properties.set("port", port);
        try{
            network.tcpServer().run(port);
            System.out.println("[INFO]: Server running on port " + port);
            this.startServerThread();
        }catch(Exception e){
            System.err.println("[ERROR]: Error running server: " + e.getMessage());
        }
    }

    public void stop() {
        System.out.println("[INFO]: Server closed.");
        network.tcpServer().close();
        if(dedicated)
            thread.interrupt();
    }

    private void startServerThread() {
        if(dedicated){
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
