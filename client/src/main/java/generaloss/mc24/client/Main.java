package generaloss.mc24.client;

import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.client.network.ClientConnection;
import generaloss.mc24.client.registry.ClientRegistries;
import generaloss.mc24.client.screen.JoiningServerScreen;
import generaloss.mc24.server.network.AccountSession;
import generaloss.mc24.server.network.packet2s.LoginRequestPacket2S;
import generaloss.mc24.server.resourcepack.ResourcePack;
import generaloss.mc24.client.screen.TitleScreen;
import generaloss.mc24.client.screen.ScreenDispatcher;
import generaloss.mc24.client.player.ClientPlayer;
import generaloss.mc24.client.screen.SessionScreen;
import generaloss.mc24.server.ArgsMap;
import generaloss.mc24.server.Server;
import jpize.app.Jpize;
import jpize.app.JpizeApplication;
import jpize.audio.AlDevices;
import jpize.gl.Gl;
import jpize.glfw.Glfw;
import jpize.glfw.init.GlfwPlatform;
import jpize.glfw.input.Key;
import jpize.util.font.Font;
import jpize.util.res.Resource;

import java.util.List;

public class Main extends JpizeApplication {

    private final ClientRegistries registries;
    private final ScreenDispatcher screens;
    private final Server localServer;
    private final WorldLevel level;
    private final ClientPlayer player;
    private final ClientConnection connection;
    private AccountSession session;

    public Main() {
        this.registries = new ClientRegistries(new ResourcePack("vanilla-pack.zip"));
        this.screens = new ScreenDispatcher();
        this.localServer = new Server(registries, false);
        this.level = new WorldLevel(this);
        this.player = new ClientPlayer(this);
        this.connection = new ClientConnection(this);
    }

    public ClientRegistries registries() {
        return registries;
    }

    public ScreenDispatcher screens() {
        return screens;
    }

    //public Server localServer() {
    //    return localServer;
    //}

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
        localServer.init();
        System.out.println("[INFO]: Initialize client");
        // audio
        AlDevices.openDevice();
        // load block models
        for(Resource blockModelRes : registries.getDefaultPack().getResource("models/blocks/").listResources())
            registries.BLOCK_MODELS.register(blockModelRes.path());
        // load font
        registries.FONTS.register("default", "fonts/default/font.fnt");
        // register screens
        screens.register(new TitleScreen(this));
        screens.register(new SessionScreen(this));
        screens.register(new JoiningServerScreen(this));
        // load all resources
        registries.loadResources();
        // set menu screen
        screens.show("title");
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
        //localServer.stop();
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
            registries.reloadResources(List.of(registries.getDefaultPack()));
            for(LevelChunk chunk: level.getChunks()){
                chunk.freeMesh();
                level.tesselators().tesselate(chunk);
                System.out.println("tesselate: resource pack");
            }
        }else if(Key.F2.up()){
            final ResourcePack testPack1 = new ResourcePack("test-pack-1.zip");
            registries.reloadResources(List.of(testPack1, registries.getDefaultPack()));
            for(LevelChunk chunk: level.getChunks()){
                chunk.freeMesh();
                level.tesselators().tesselate(chunk);
                System.out.println("tesselate: resource pack");
            }
        }else if(Key.F3.up()){
            final ResourcePack testPack2 = new ResourcePack("test-pack-2.zip");
            registries.reloadResources(List.of(testPack2, registries.getDefaultPack()));
            for(LevelChunk chunk: level.getChunks()){
                chunk.freeMesh();
                level.tesselators().tesselate(chunk);
                System.out.println("tesselate: resource pack");
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