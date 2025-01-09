package generaloss.mc24.client;

import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.client.network.ClientConnection;
import generaloss.mc24.client.registry.ClientRegistries;
import generaloss.mc24.client.screen.JoiningServerScreen;
import generaloss.mc24.server.network.AccountSession;
import generaloss.mc24.server.network.packet2s.LoginRequestPacket2S;
import generaloss.mc24.server.registry.ServerRegistries;
import generaloss.mc24.client.screen.MainMenuScreen;
import generaloss.mc24.client.screen.ScreenDispatcher;
import generaloss.mc24.client.player.ClientPlayer;
import generaloss.mc24.client.screen.SessionScreen;
import generaloss.mc24.server.ArgsMap;
import generaloss.mc24.server.Server;
import generaloss.mc24.server.resourcepack.ResourcePackManager;
import jpize.app.Jpize;
import jpize.app.JpizeApplication;
import jpize.audio.AlDevices;
import jpize.gl.Gl;
import jpize.glfw.Glfw;
import jpize.glfw.init.GlfwPlatform;
import jpize.glfw.input.Key;
import jpize.util.font.Font;

public class Main extends JpizeApplication {

    private final ResourcePackManager resourcePackManager;
    private final ClientRegistries registries;
    private final ScreenDispatcher screens;
    private final Server localServer;
    private final WorldLevel level;
    private final ClientPlayer player;
    private final ClientConnection connection;
    private AccountSession session;

    public Main() {
        this.resourcePackManager = new ResourcePackManager();
        this.registries = new ClientRegistries(resourcePackManager);
        this.screens = new ScreenDispatcher();
        this.localServer = new Server(resourcePackManager, false);
        this.level = new WorldLevel(this);
        this.player = new ClientPlayer(this);
        this.connection = new ClientConnection(this);
    }

    public ResourcePackManager resourcePackManager() {
        return resourcePackManager;
    }

    public ClientRegistries registries() {
        return registries;
    }

    public ScreenDispatcher screens() {
        return screens;
    }

    public Server localServer() {
        return localServer;
    }

    public WorldLevel level() {
        return level;
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
        // server
        localServer.init();
        // audio
        AlDevices.openDevice();
        // font
        registries.FONTS.register("default", "fonts/default/font.fnt");

        // blockstate models
        ServerRegistries.BLOCK_STATE.addRegisterCallback(blockstate ->
            registries.BLOCK_STATE_MODELS.register(blockstate, resourcePackManager)
        );

        // screens
        screens.register(new MainMenuScreen(this));
        screens.register(new SessionScreen(this));
        screens.register(new JoiningServerScreen(this));

        // load all resources
        ServerRegistries.loadResources();
        registries.loadResources();

        // set menu screen
        screens.show("main_menu");
    }


    public void connectSession(String host, int port) {
        //if(host.equals("localhost"))
        //    localServer.run(port);
        System.out.println("[INFO]: Connecting to server " + host + ":" + port);
        connection.connect(host, port);

        screens.show("joining_server");

        connection.sendPacket(new LoginRequestPacket2S("24.11.5"));
    }

    public void disconnectSession() {
        connection.disconnect();
        // localServer.stop();
        player.input().disable();
        level.reset();
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
            registries.reloadResources();

            for(LevelChunk chunk: level.getChunks()){
                chunk.freeMesh();
                level.tesselators().tesselate(chunk);
            }
        }else if(Key.F2.up()){
            resourcePackManager.clear();
            resourcePackManager.putPack("test-pack-1.zip");
            registries.reloadResources();

            for(LevelChunk chunk: level.getChunks()){
                chunk.freeMesh();
                level.tesselators().tesselate(chunk);
            }
        }else if(Key.F3.up()){
            resourcePackManager.clear();
            resourcePackManager.putPack("test-pack-2.zip");
            registries.reloadResources();

            for(LevelChunk chunk: level.getChunks()){
                chunk.freeMesh();
                level.tesselators().tesselate(chunk);
            }
        }

        // secreens
        screens.update();

        // font
        final Font font = registries.FONTS.get("default");
        font.getRenderOptions().scale().set(Jpize.getHeight() / font.getHeight() * 0.03F);
    }

    @Override
    public void render() {
        Gl.clearColorDepthBuffers();
        screens.render();
        final Font font = registries.FONTS.get("default");
        font.drawText("FPS: " + Jpize.getFPS(), 10F, Jpize.getHeight() - 10F - font.getLineAdvanceScaled());
    }

    @Override
    public void resize(int width, int height) {
        screens.resize(width, height);
    }

    @Override
    public void dispose() {
        level.dispose();
        screens.dispose();
        registries.dispose();
        AlDevices.dispose();
    }


    public static void main(String[] args) {
        // wayland=>x11 fix
        if(System.getProperty("os.name").equals("Linux"))
            Glfw.glfwInitHintPlatform(GlfwPlatform.X11);

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