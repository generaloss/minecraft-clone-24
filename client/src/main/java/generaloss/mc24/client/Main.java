package generaloss.mc24.client;

import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.client.network.ClientConnection;
import generaloss.mc24.client.resource.ClientResources;
import generaloss.mc24.client.resource.BlockModelHandle;
import generaloss.mc24.client.screen.JoiningServerScreen;
import generaloss.mc24.server.SharedConstants;
import generaloss.mc24.server.entity.EntityContainer;
import generaloss.mc24.server.network.AccountSession;
import generaloss.mc24.server.network.packet2s.LoginRequestPacket2S;
import generaloss.mc24.server.registry.ServerRegistries;
import generaloss.mc24.client.screen.MainMenuScreen;
import generaloss.mc24.client.player.ClientPlayer;
import generaloss.mc24.client.screen.SessionScreen;
import generaloss.mc24.server.ArgsMap;
import generaloss.mc24.server.Server;
import generaloss.mc24.server.resourcepack.ResourcePack;
import generaloss.mc24.server.resourcepack.ResourcePackManager;
import jpize.app.Jpize;
import jpize.app.JpizeApplication;
import jpize.audio.AlDevices;
import jpize.gl.Gl;
import jpize.glfw.input.Key;
import jpize.util.font.Font;
import jpize.util.screen.ScreenManager;

public class Main extends JpizeApplication {

    private final ResourcePackManager resourcePackManager;
    private final ScreenManager<String> screens;
    private final Server localServer;
    private final WorldLevel level;
    private final EntityContainer entities;
    private final ClientPlayer player;
    private final ClientConnection connection;
    private AccountSession session;

    public Main() {
        this.resourcePackManager = new ResourcePackManager();
        ClientResources.init(resourcePackManager);
        this.screens = new ScreenManager<>();
        this.localServer = new Server(resourcePackManager, false);
        this.level = new WorldLevel(this);
        this.entities = new EntityContainer();
        this.player = new ClientPlayer(this);
        this.connection = new ClientConnection(this);
    }

    public ResourcePackManager resourcePackManager() {
        return resourcePackManager;
    }

    public ScreenManager<String> screens() {
        return screens;
    }

    public Server localServer() {
        return localServer;
    }

    public WorldLevel level() {
        return level;
    }

    public EntityContainer entities() {
        return entities;
    }

    public ClientPlayer player() {
        return player;
    }

    public ClientConnection connection() {
        return connection;
    }


    public AccountSession session() {
        return session;
    }

    public void setSession(AccountSession session) {
        this.session = session;
    }


    @Override
    public void init() {
        // audio
        AlDevices.openDevice();
        // font
        ClientResources.FONTS.load("default", "fonts/default/font.fnt");

        // blockstate models
        ServerRegistries.BLOCK_STATE.addRegisterCallback(blockstate -> {
            System.out.println("loaded block state: " + blockstate);
            ClientResources.BLOCK_STATE_MODELS.load(
                new BlockModelHandle(blockstate, "blocks/" + blockstate.getBlockID() + ".json")
            );
        });

        // server
        localServer.init();

        // screens
        screens.register(MainMenuScreen.SCREEN_ID, new MainMenuScreen(this));
        screens.register(SessionScreen.SCREEN_ID, new SessionScreen(this));
        screens.register(JoiningServerScreen.SCREEN_ID, new JoiningServerScreen(this));

        // set menu screen
        screens.setCurrent(MainMenuScreen.SCREEN_ID);
    }


    public void connectSession(String host, int port) {
        //if(host.equals("localhost"))
        //    localServer.run(port);
        System.out.println("[INFO]: Connecting to server " + host + ":" + port);
        connection.connect(host, port);

        screens.setCurrent(JoiningServerScreen.SCREEN_ID);

        connection.sendPacket(new LoginRequestPacket2S(SharedConstants.VERSION));
    }

    public void disconnectSession() {
        connection.disconnect();
        this.closeSession();
        // localServer.stop();
    }

    public void closeSession() {
        player.input().disable();
        level.reset();
        entities.clear();
        screens.setCurrent(MainMenuScreen.SCREEN_ID);
        System.out.println("[INFO]: Disconnect session");
    }


    @Override
    public void update() {
        // fullscreen
        if(Key.F11.down()){
            Jpize.window().toggleFullscreen();
            player.input().lockInputs();
        }

        // resource pack
        if(Key.F1.up()){
            resourcePackManager.clear();
            ClientResources.reload();

            for(LevelChunk chunk: level.getChunks()){
                chunk.freeMesh();
                level.tesselators().tesselate(chunk);
            }
        }else if(Key.F2.up()){
            resourcePackManager.clear().putPack(new ResourcePack("test-pack-1.zip"));
            ClientResources.reload();

            for(LevelChunk chunk: level.getChunks()){
                chunk.freeMesh();
                level.tesselators().tesselate(chunk);
            }
        }else if(Key.F3.up()){
            resourcePackManager.clear();
            resourcePackManager.putPack(new ResourcePack("test-pack-2.zip"));
            ClientResources.reload();

            for(LevelChunk chunk: level.getChunks()){
                chunk.freeMesh();
                level.tesselators().tesselate(chunk);
            }
        }

        // secreens
        screens.update();

        // font
        final Font font = ClientResources.FONTS.get("default").resource();
        font.getRenderOptions().scale().set(Jpize.getHeight() / font.getHeight() * 0.03F);
    }

    @Override
    public void render() {
        Gl.clearColorDepthBuffers();
        screens.render();
        final Font font = ClientResources.FONTS.get("default").resource();
        font.drawText("FPS: " + Jpize.getFPS(), 10F, Jpize.getHeight() - 10F - font.getLineAdvanceScaled());
    }

    @Override
    public void resize(int width, int height) {
        screens.resize(width, height);
    }

    @Override
    public void dispose() {
        if(session != null)
            session.logout("qwe");
        level.dispose();
        screens.dispose();
        ClientResources.dispose();
        AlDevices.dispose();
    }


    public static void main(String[] args) {
        // parse arguments
        final ArgsMap argsMap = new ArgsMap(args);
        final int width = argsMap.getInt("width", 1280);
        final int height = argsMap.getInt("height", 720);

        // create window
        Jpize.create("Minecraft-24 Client", width, height)
            .icon("/icon.png")
            .build()
            .setApp(INSTANCE = new Main());

        // run application
        Jpize.run();
    }

    private static Main INSTANCE;

    public static Main getInstance() {
        return INSTANCE;
    }

}