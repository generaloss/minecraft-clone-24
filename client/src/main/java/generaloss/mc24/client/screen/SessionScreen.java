package generaloss.mc24.client.screen;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.player.BlockSelectRay;
import generaloss.mc24.client.player.ClientPlayer;
import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockState;
import jpize.app.Jpize;
import jpize.gl.Gl;
import jpize.gl.glenum.GlTarget;
import jpize.gl.texture.Texture2D;
import jpize.glfw.input.Key;
import jpize.glfw.input.MouseBtn;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.font.Font;
import jpize.util.math.vector.Vec2f;
import jpize.util.math.vector.Vec3i;
import jpize.util.mesh.TextureBatch;

public class SessionScreen extends IScreen {

    private final TextureBatch batch;
    private final Texture2D crosshair;
    private String toPlaceBlockID = "stone";

    public SessionScreen(Main context) {
        super(context, "session");

        this.batch = new TextureBatch();

        this.crosshair = super.context().registries().TEXTURES
            .register("crosshair", "textures/gui/hud/crosshair.png")
            .getObject();
    }

    @Override
    public void show() {
        Gl.clearColor(0.25F, 0.35F, 0.5F); // sky color
        final PerspectiveCamera camera = super.context().player().camera();
        camera.resize(Jpize.getWidth(), Jpize.getHeight());
        super.context().player().input().enable();
    }

    @Override
    public void update() {
        // exit to main_menu screen
        if(Key.ESCAPE.down()){
            super.context().disconnectSession();
            super.context().screens().show("main_menu");
        }

        // tesselate chunk meshes
        super.context().level().update();

        // player
        final ClientPlayer player = super.context().player();
        player.update();

        // select block to place
        if(Key.NUM_1.down()) {
            toPlaceBlockID = "stone";
        }else if(Key.NUM_2.down()) {
            toPlaceBlockID = "grass_block";
        }else if(Key.NUM_3.down()) {
            toPlaceBlockID = "dirt";
        }else if(Key.NUM_4.down()) {
            toPlaceBlockID = "torch";
        }else if(Key.NUM_5.down()) {
            toPlaceBlockID = "redstone_torch";
        }else if(Key.NUM_6.down()) {
            toPlaceBlockID = "stairs";
        }

        final BlockSelectRay ray = player.blockSelectRay();
        if(ray.hasSelection()){
            if(MouseBtn.LEFT.down()){
                // destroy
                final Vec3i position = ray.getDestroyPosition();
                super.context().level().setBlockState(position.x, position.y, position.z, Block.AIR.getDefaultState());
            }else if(MouseBtn.RIGHT.down()){
                // place
                final Vec3i position = ray.getPlacePosition();
                final BlockState blockstate = super.context().registries().BLOCKS.get(toPlaceBlockID).getDefaultState();
                super.context().level().setBlockState(position.x, position.y, position.z, blockstate);
            }else if(MouseBtn.MIDDLE.down()){
                // pick
                final Vec3i position = ray.getDestroyPosition();
                final BlockState blockstate = super.context().level().getBlockState(position.x, position.y, position.z);
                toPlaceBlockID = blockstate.getBlockID();
            }
        }
    }

    @Override
    public void render() {
        // level
        Gl.enable(GlTarget.DEPTH_TEST);
        final PerspectiveCamera camera = super.context().player().camera();
        super.context().level().render(camera);

        // hud
        Gl.disable(GlTarget.DEPTH_TEST);
        batch.setup();

        // crosshair
        final float crosshairSize = Jpize.getHeight() * 0.03F;
        final float crosshairX = (Jpize.getWidth() - crosshairSize) * 0.5F;
        final float crosshairY = (Jpize.getHeight() - crosshairSize) * 0.5F;
        batch.draw(crosshair, crosshairX, crosshairY, crosshairSize, crosshairSize);

        // place block ID
        final Font font = super.context().registries().FONTS.get("default");
        final String selectedBlockText = ("Selected block: " + toPlaceBlockID);
        final Vec2f selectedTextBounds = font.getTextBounds(selectedBlockText);
        final float selectedTextX = (Jpize.getWidth() - selectedTextBounds.x);
        final float selectedTextY = (Jpize.getHeight() - selectedTextBounds.y);
        font.drawText(batch, selectedBlockText, selectedTextX, selectedTextY);

        batch.render(false);
    }

    @Override
    public void resize(int width, int height) {
        final PerspectiveCamera camera = super.context().player().camera();
        camera.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

}