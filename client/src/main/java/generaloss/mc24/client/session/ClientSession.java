package generaloss.mc24.client.session;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.client.resource.ResourceDispatcher;
import jpize.audio.util.AlMusic;
import jpize.gl.Gl;
import jpize.gl.glenum.GlTarget;
import jpize.gl.texture.Texture2D;
import jpize.glfw.input.Key;
import jpize.util.mesh.TextureBatch;
import jpize.util.res.Resource;

public class ClientSession {

    private final Main context;

    private final AlMusic music;

    private final WorldLevel level;
    private final ClientPlayer player;

    private final TextureBatch batch = new TextureBatch();
    private final Texture2D texture = new Texture2D(Resource.external("assets/vanilla-pack/textures/blocks/torch.png"));

    public ClientSession(Main context) {
        this.context = context;

        // resources
        final ResourceDispatcher resources = context.resources();

        this.music = resources
            .registerMusic("game_music", "/music/subwoofer_lullaby.ogg")
            .resource();

        // camera
        this.level = new WorldLevel(context);
        this.player = new ClientPlayer();
    }


    public WorldLevel level() {
        return level;
    }

    public ClientPlayer player() {
        return player;
    }


    public void connect(String host, int port) {
        music.play();
        player.input().enable();
        Gl.enable(GlTarget.DEPTH_TEST);
    }

    public void close() {
        music.pause();
        player.input().disable();
        level.dispose();
        Gl.disable(GlTarget.DEPTH_TEST);
        System.out.println("close");
    }


    public void update() {
        // music
        music.update();
        // player
        player.update();
        // exit to title screen
        if(Key.ESCAPE.down())
            context.screens().show("title");
        // tesselate chunk meshes
        level.tesselator().update();
    }

    public void render() {
        Gl.clearDepthBuffer();
        level.renderer().render(player.camera());
    }

    public void resize(int width, int height) {
        player.camera().resize(width, height);
    }

}
