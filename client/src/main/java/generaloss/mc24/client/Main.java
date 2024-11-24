package generaloss.mc24.client;

import generaloss.mc24.client.resource.ResourcesRegistryClient;
import generaloss.mc24.client.screen.TitleScreen;
import generaloss.mc24.client.screen.ScreenDispatcher;
import generaloss.mc24.client.session.ClientSession;
import generaloss.mc24.client.session.SessionScreen;
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
import jpize.util.font.FontLoader;
import jpize.util.math.Maths;
import jpize.util.res.ExternalResource;
import jpize.util.res.Resource;

public class Main extends JpizeApplication {

    private final ResourcesRegistryClient resources;
    private final Server localServer;
    private final Font font;
    private final ScreenDispatcher screens;
    private final ClientSession session;

    public Main() {
        this.resources = new ResourcesRegistryClient();
        this.localServer = new Server(resources);
        this.font = FontLoader.loadDefault();
        this.screens = new ScreenDispatcher();
        this.session = new ClientSession(this);
    }

    public ResourcesRegistryClient resources() {
        return resources;
    }

    public Server localServer() {
        return localServer;
    }

    public ScreenDispatcher screens() {
        return screens;
    }


    @Override
    public void init() {
        System.out.println("Initializing client");
        // audio
        AlDevices.openDevice();
        // screens
        screens.register(new TitleScreen(this));
        screens.register(new SessionScreen(this, session));
        // resources
        this.loadResources();
        // set menu screen
        screens.show("title");
        startLocalSession();
    }


    private void loadResources() {
        // load block models
        final String blockModelsPath = "/resources/models/blocks/";
        final ExternalResource defaultBlockModelsRes = Resource.external(resources.getDefaultDirectory() + blockModelsPath);
        System.out.println("Loading " + defaultBlockModelsRes.list().length + " block models..");
        for(ExternalResource blockModelRes: defaultBlockModelsRes.listRes())
            resources.registerBlock(blockModelRes.simpleName(), blockModelsPath + blockModelRes.name());
        // apply default asset pack
        resources.reloadAll();
    }


    public void startLocalSession() {
        System.out.println("Starting local session..");
        localServer.run(Maths.random(64000, 64999));
        session.connect("localhost", localServer.getPort());
        screens.show("session");
    }


    @Override
    public void update() {
        // --- reload resources test --- //
        if(Key.NUM_0.up()){
            resources.resetDirectory();
            resources.reloadAll();
        }
        if(Key.NUM_1.up()){
            resources.setDirectory("resource-packs/test-pack-1");
            resources.reloadAll();
        }
        if(Key.NUM_2.up()){
            resources.setDirectory("resource-packs/test-pack-2");
            resources.reloadAll();
        }

        // fullscreen
        if(Key.F11.down())
            Jpize.window().toggleFullscreen();

        // secreens
        screens.update();
    }

    @Override
    public void render() {
        Gl.clearColorBuffer();
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
        resources.dispose();
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