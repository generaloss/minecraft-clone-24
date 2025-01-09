package generaloss.mc24.client.screen;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.player.BlockSelectRay;
import generaloss.mc24.client.player.ClientPlayer;
import generaloss.mc24.server.Facing;
import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.block.StateProperty;
import generaloss.mc24.server.registry.ServerRegistries;
import jpize.app.Jpize;
import jpize.gl.Gl;
import jpize.gl.glenum.GlTarget;
import jpize.gl.texture.Texture2D;
import jpize.glfw.input.Key;
import jpize.glfw.input.MouseBtn;
import jpize.util.array.StringList;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.font.Font;
import jpize.util.math.Maths;
import jpize.util.math.vector.Vec2f;
import jpize.util.math.vector.Vec3i;
import jpize.util.mesh.TextureBatch;

import java.util.concurrent.atomic.AtomicInteger;

public class SessionScreen extends IScreen {

    private final TextureBatch batch;
    private final Texture2D crosshair;
    private BlockState toPlaceBlockState;

    public SessionScreen(Main context) {
        super(context, "session");

        this.batch = new TextureBatch();

        this.crosshair = super.context().registries().TEXTURES
            .register("crosshair", "textures/gui/hud/crosshair.png")
            .getObject();
    }

    @Override
    public void init() {
        this.toPlaceBlockState = ServerRegistries.BLOCK.get("stone").getDefaultState();
    }

    @Override
    public void show() {
        Gl.clearColor(0.25F, 0.35F, 0.5F); // sky color
        final PerspectiveCamera camera = super.context().player().camera();
        camera.resize(Jpize.getWidth(), Jpize.getHeight());
        super.context().player().input().enable();
    }

    public static boolean AO = true;

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
            toPlaceBlockState = ServerRegistries.BLOCK.get("stone").getDefaultState();
        }else if(Key.NUM_2.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("grass_block").getDefaultState();
        }else if(Key.NUM_3.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("dirt").getDefaultState();
        }else if(Key.NUM_4.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("stone_stairs").getDefaultState();
        }else if(Key.NUM_5.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("torch").getDefaultState();
        }else if(Key.NUM_6.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("wall_torch").getDefaultState();
        }else if(Key.NUM_7.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("redstone_torch").getDefaultState();
        }else if(Key.NUM_8.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("redstone_wall_torch").getDefaultState();
        }else if(Key.NUM_9.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("red_shroom_0").getDefaultState();
        }else if(Key.NUM_0.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("campfire").getDefaultState();
        }

        if(Key.F5.down()) {
            AO = true;
        }else if(Key.F6.down()) {
            AO = false;
        }

        final BlockSelectRay ray = player.blockSelectRay();
        if(ray.hasSelection()){
            if(MouseBtn.LEFT.down()){
                // destroy
                final Vec3i position = ray.getDestroyPosition();
                super.context().level().setBlockState(position.x, position.y, position.z, Block.AIR.getDefaultState());

            }else if(MouseBtn.RIGHT.down()){
                // facing
                Facing facing = Facing.byAngle(player.camera().rotation().getYaw());
                if(toPlaceBlockState.isBlockID("stone_stairs"))
                    facing = facing.opposite();

                // blockstate properties
                toPlaceBlockState = toPlaceBlockState.withProperty("bites", Maths.random(0, 4));
                toPlaceBlockState = toPlaceBlockState.withProperty("shape", "straight");
                toPlaceBlockState = toPlaceBlockState.withProperty("facing", facing);
                toPlaceBlockState = toPlaceBlockState.withProperty("lit", true);

                // place
                final Vec3i position = ray.getPlacePosition();
                super.context().level().setBlockState(position.x, position.y, position.z, toPlaceBlockState);

            }else if(MouseBtn.MIDDLE.down()){
                // pick
                final Vec3i position = ray.getDestroyPosition();
                toPlaceBlockState = super.context().level().getBlockState(position.x, position.y, position.z);
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
        final String selectedBlockText = ("Selected block: " + toPlaceBlockState.getBlockID());
        final Vec2f selectedBlockTextBounds = font.getTextBounds(selectedBlockText);
        final float selectedBlockTextX = (Jpize.getWidth() - selectedBlockTextBounds.x);
        final float selectedBlockTextY = (Jpize.getHeight() - selectedBlockTextBounds.y);
        font.drawText(batch, selectedBlockText, selectedBlockTextX, selectedBlockTextY);

        // place block ID
        final BlockSelectRay ray = super.context().player().blockSelectRay();
        if(ray.hasSelection()){
            final StateProperty<?>[] keys = ray.getBlockState().getStateProperties().keySet().toArray(new StateProperty[0]);
            AtomicInteger i = new AtomicInteger();
            final String aimBlockstateText = (
                "Blockstate: '" + ray.getBlockState().getBlockID() +
                "' " + new StringList(ray.getBlockState().getStateProperties().values(), (v) -> keys[i.getAndIncrement()].getName() + "=" + v
            ));
            final Vec2f aimBlockstateTextBounds = font.getTextBounds(aimBlockstateText);
            final float aimBlockstateTextX = (Jpize.getWidth() - aimBlockstateTextBounds.x);
            final float aimBlockstateTextY = (Jpize.getHeight() - aimBlockstateTextBounds.y - font.getLineAdvanceScaled());
            font.drawText(batch, aimBlockstateText, aimBlockstateTextX, aimBlockstateTextY);
        }

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