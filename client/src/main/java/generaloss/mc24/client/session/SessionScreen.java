package generaloss.mc24.client.session;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.screen.IScreen;
import jpize.glfw.input.Key;

public class SessionScreen extends IScreen {

    public SessionScreen(Main context) {
        super(context, "session");
    }

    @Override
    public void update() {
        // player
        context().player().update();
        // exit to title screen
        if(Key.ESCAPE.down())
            context().screens().show("title");
        // tesselate chunk meshes
        context().level().tesselator().update();
    }

    @Override
    public void render() {
        context().level().renderer().render(context().player().camera());
    }

    @Override
    public void resize(int width, int height) {
        context().player().camera().resize(width, height);
    }

}