package generaloss.mc24.client;

import generaloss.mc24.client.menu.MainMenuScreen;
import generaloss.mc24.client.resource.ResourceDispatcher;
import generaloss.mc24.client.screen.ScreenDispatcher;
import generaloss.mc24.client.session.SessionScreen;
import generaloss.mc24.server.ArgsMap;
import jpize.app.Jpize;
import jpize.app.JpizeApplication;
import jpize.audio.Audio;
import jpize.gl.Gl;
import jpize.glfw.Glfw;
import jpize.glfw.init.GlfwPlatform;
import jpize.glfw.input.Key;

public class Main extends JpizeApplication {

    private final ScreenDispatcher screens;
    private final ResourceDispatcher resources;

    public Main() {
        this.screens = new ScreenDispatcher();
        this.resources = new ResourceDispatcher();
    }

    public ScreenDispatcher screens() {
        return screens;
    }

    public ResourceDispatcher resources() {
        return resources;
    }


    public void startSession() {
        screens.show("session");
    }


    @Override
    public void init() {
        System.out.println("Initializing");
        // audio
        Audio.init();
        Audio.openDevice();
        // screens
        screens.register(new MainMenuScreen(this));
        screens.register(new SessionScreen(this));
        // resources
        resources.reloadAll();
        // set menu screen
        screens.show("main_menu");
    }

    @Override
    public void render() {
        Gl.clearColorBuffer();
        screens.render();
    }

    @Override
    public void update() {
        // --- reload resources test --- //
        if(Key.NUM_9.up()){
            resources.setRootDirectory("resource-packs/test-pack");
            resources.reloadAll();
        }
        if(Key.NUM_8.up()){
            resources.setRootDirectory("assets/default-resources");
            resources.reloadAll();
        }
        // --- end --- //

        screens.update();
    }

    @Override
    public void resize(int width, int height) {
        screens.resize(width, height);
    }

    @Override
    public void dispose() {
        screens.dispose();
        resources.dispose();
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