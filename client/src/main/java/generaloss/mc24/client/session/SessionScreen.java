package generaloss.mc24.client.session;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.client.render.SessionRenderers;
import generaloss.mc24.client.resource.ResourceDispatcher;
import generaloss.mc24.client.screen.IScreen;
import jpize.app.Jpize;
import jpize.audio.util.AlMusic;
import jpize.gl.texture.Skybox;
import jpize.glfw.input.Key;

public class SessionScreen extends IScreen {

    private final AlMusic music;
    private final Skybox skybox;
    private final WorldLevel level;
    private final ClientPlayer player;
    private final SessionRenderers renderers;

    public SessionScreen(Main context) {
        super(context, "session");

        // resources
        final ResourceDispatcher resources = super.context().resources();

        this.music = resources
                .registerMusic("game_music", "/music/subwoofer_lullaby.ogg")
                .resource();

        this.skybox = resources
                .registerSkybox("game_skybox", "/textures/panorama/skybox_%s.png")
                .resource();

        // camera
        this.level = new WorldLevel();
        this.player = new ClientPlayer();
        this.renderers = new SessionRenderers(context, this);
    }

    public WorldLevel level() {
        return level;
    }

    public ClientPlayer player() {
        return player;
    }

    public SessionRenderers renderers() {
        return renderers;
    }


    @Override
    public void show() {
        System.out.println("Show session screen");
        music.play();
        player.input().enable();
    }

    @Override
    public void hide() {
        music.pause();
        player.input().disable();
    }

    @Override
    public void update() {
        // music
        music.update();
        // player
        player.update();
        // exit
        if(Key.ESCAPE.down())
            Jpize.exit();
    }

    @Override
    public void render() {
        skybox.render(player.camera());
        renderers.render();
    }

    @Override
    public void resize(int width, int height) {
        player.camera().resize(width, height);
    }

}