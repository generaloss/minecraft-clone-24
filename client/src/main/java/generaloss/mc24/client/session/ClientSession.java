package generaloss.mc24.client.session;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.level.WorldLevel;
import jpize.gl.Gl;
import jpize.gl.glenum.GlTarget;
import jpize.glfw.input.Key;

public class ClientSession {

    private final Main context;

    private final WorldLevel level;
    private final ClientPlayer player;

    public ClientSession(Main context) {
        this.context = context;
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
        player.input().enable();
        Gl.enable(GlTarget.DEPTH_TEST);
    }

    public void close() {
        player.input().disable();
        level.dispose();
        Gl.disable(GlTarget.DEPTH_TEST);
        System.out.println("close");
    }


    public void update() {
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
