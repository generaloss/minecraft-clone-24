package generaloss.mc24.client;

import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.client.registry.ClientRegistries;
import generaloss.mc24.server.resourcepack.ResourcePack;
import generaloss.mc24.client.screen.TitleScreen;
import generaloss.mc24.client.screen.ScreenDispatcher;
import generaloss.mc24.client.session.ClientPlayer;
import generaloss.mc24.client.session.SessionScreen;
import generaloss.mc24.server.ArgsMap;
import generaloss.mc24.server.Server;
import jpize.app.Jpize;
import jpize.app.JpizeApplication;
import jpize.audio.AlDevices;
import jpize.gl.Gl;
import jpize.gl.glenum.GlTarget;
import jpize.glfw.Glfw;
import jpize.glfw.init.GlfwPlatform;
import jpize.glfw.input.Key;
import jpize.util.font.Font;
import jpize.util.font.FontLoader;
import jpize.util.math.Maths;
import jpize.util.res.Resource;

import java.util.List;

public class Main extends JpizeApplication {

    private final Font font;
    private final ResourcePack defaultPack;
    private final ClientRegistries registries;
    private final ScreenDispatcher screens;
    private final Server localServer;
    private final WorldLevel level;
    private final ClientPlayer player;

    public Main() {
        this.font = FontLoader.loadDefault();
        this.defaultPack = new ResourcePack("vanilla-pack.zip");
        this.registries = new ClientRegistries(defaultPack);
        this.screens = new ScreenDispatcher();
        this.localServer = new Server(registries);
        this.level = new WorldLevel(this);
        this.player = new ClientPlayer();
    }

    public Font font() {
        return font;
    }

    public ResourcePack getDefaultPack() {
        return defaultPack;
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


    @Override
    public void init() {
        System.out.println("Initializing client");
        localServer.init();
        // blocks
        this.loadBlockModels();
        // audio
        AlDevices.openDevice();
        // screens
        screens.register(new TitleScreen(this));
        screens.register(new SessionScreen(this));
        // load resources
        registries.loadResources();
        // set menu screen
        screens.show("title");
        this.connectLocalSession();
    }


    private void loadBlockModels() {
        for(Resource blockModelRes : registries.getDefaultPack().getResource("models/blocks/").listRes())
            registries.registerBlockModel(blockModelRes.path());
    }


    public void connectLocalSession() {
        System.out.println("Connecting local session..");
        localServer.run(Maths.random(64000, 64999));
        player.input().enable();
        Gl.enable(GlTarget.DEPTH_TEST);
        level.loadChunks();
        screens.show("session");
    }

    public void disconnectSession() {
        localServer.tcpServer().close();
        player.input().disable();
        level.dispose();
        Gl.disable(GlTarget.DEPTH_TEST);
        System.out.println("Disconnect session");
    }


    @Override
    public void update() {
        // fullscreen
        if(Key.F11.down())
            Jpize.window().toggleFullscreen();

        // resource pack
        if(Key.NUM_0.up()){
            registries.reloadResources(List.of(defaultPack));
        }else if(Key.NUM_1.up()){
            final ResourcePack testPack1 = new ResourcePack("test-pack-1.zip");
            registries.reloadResources(List.of(testPack1, defaultPack));
        }else if(Key.NUM_2.up()){
            final ResourcePack testPack1 = new ResourcePack("test-pack-2.zip");
            registries.reloadResources(List.of(testPack1, defaultPack));
        }

        if(Key.B.down()) {
            final LevelChunk chunk = level.getChunk(0, 0, 0);
            for(int i = 0; i < 100; i++)
                chunk.setBlockState(Maths.random(1, 14), Maths.random(1, 14), Maths.random(1, 14), registries.getBlock("stairs").getDefaultState());
            level.tesselator().tesselate(chunk);
        }

        // secreens
        screens.update();
    }

    @Override
    public void render() {
        Gl.clearColorDepthBuffers();
        screens.render();
        font.drawText("FPS: " + Jpize.getFPS(), 10, Jpize.getHeight() - 10 - font.getLineAdvanceScaled());
    }

    @Override
    public void resize(int width, int height) {
        screens.resize(width, height);
    }

    @Override
    public void dispose() {
        screens.dispose();
        registries.dispose();
        font.dispose();
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