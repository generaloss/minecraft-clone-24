package generaloss.mc24.client.session;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.resource.ResourceDispatcher;
import generaloss.mc24.client.screen.IScreen;
import jpize.audio.util.AlMusic;
import jpize.gl.texture.Skybox;

public class SessionScreen extends IScreen {

    private final Skybox skybox;
    private final ClientPlayer player;
    private final AlMusic music;

    public SessionScreen(Main context) {
        super(context, "session");

        // resources
        final ResourceDispatcher resources = super.context().resources();

        this.skybox = resources
            .registerSkybox("game_skybox", "/textures/panorama/skybox_%s.png")
            .resource();

        this.music = resources
            .registerMusic("game_music", "/music/subwoofer_lullaby.ogg")
            .resource();

        // camera
        this.player = new ClientPlayer();
    }

    @Override
    public void init() { }

    @Override
    public void show() {
        System.out.println("Show session screen");
        music.play();
    }

    @Override
    public void hide() {
        music.pause();
    }

    @Override
    public void update() {
        // music
        music.update();
        // player
        player.update();
    }

    @Override
    public void render() {
        skybox.render(player.camera());
    }

    @Override
    public void resize(int width, int height) {
        player.camera().resize(width, height);
    }

}