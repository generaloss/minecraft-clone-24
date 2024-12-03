package generaloss.mc24.client.screen;

import generaloss.mc24.client.Main;
import jpize.gl.Gl;
import jpize.gl.glenum.GlTarget;
import jpize.glfw.input.Key;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.math.vector.Vec3f;

public class SessionScreen extends IScreen {

    public SessionScreen(Main context) {
        super(context, "session");
    }

    @Override
    public void show() {
        //Gl.clearColor(0.5F, 0.7F, 1F); // blue sky color ingame
    }

    @Override
    public void update() {
        // player
        context().player().update();
        // exit to title screen
        if(Key.ESCAPE.down()){
            super.context().disconnectSession();
            context().screens().show("title");
        }
        // tesselate chunk meshes
        context().level().update();

        // place stairs
        if(Key.K.down()) {
            final Vec3f pos = super.context().player().camera().position();
            super.context().level().setBlockState(pos.xFloor(), pos.yFloor() - 3, pos.zFloor(),
                super.context().registries().getBlock("stairs").getDefaultState());
        }

        // place torhces
        if(Key.L.down()) {
            final Vec3f pos = super.context().player().camera().position();
            super.context().level().setBlockState(pos.xFloor(), pos.yFloor() - 3, pos.zFloor(),
                super.context().registries().getBlock("torch").getDefaultState());
        }
    }

    @Override
    public void render() {
        Gl.enable(GlTarget.DEPTH_TEST);
        final PerspectiveCamera camera = super.context().player().camera();
        super.context().level().render(camera);
        Gl.disable(GlTarget.DEPTH_TEST);
    }

    @Override
    public void resize(int width, int height) {
        final PerspectiveCamera camera = super.context().player().camera();
        camera.resize(width, height);
    }

}