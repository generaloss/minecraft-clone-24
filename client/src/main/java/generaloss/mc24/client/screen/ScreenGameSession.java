package generaloss.mc24.client.screen;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.player.BlockSelectRay;
import generaloss.mc24.client.player.ClientPlayer;
import generaloss.mc24.client.resources.ClientResources;
import generaloss.mc24.server.common.Facing;
import generaloss.mc24.server.block.Block;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.block.StateProperty;
import generaloss.mc24.server.entity.AbstractEntity;
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
import jpize.util.font.FontRenderOptions;
import jpize.util.math.Maths;
import jpize.util.math.vector.Vec2f;
import jpize.util.math.vector.Vec3f;
import jpize.util.math.vector.Vec3i;
import jpize.util.mesh.TextureBatch;

import java.util.concurrent.atomic.AtomicInteger;

public class ScreenGameSession extends Screen {

    public static final String SCREEN_ID = "game_session";

    private final TextureBatch batch;
    private final Texture2D crosshair;
    private BlockState toPlaceBlockState;

    public ScreenGameSession(Main context) {
        super(context, SCREEN_ID);
        this.batch = new TextureBatch();
        this.crosshair = ClientResources.TEXTURES.create("crosshair", "textures/gui/hud/crosshair.png").resource();
    }

    @Override
    public void init() {
        this.toPlaceBlockState = ServerRegistries.BLOCK.get("stone").resource().getDefaultState();
    }

    @Override
    public void show() {
        Gl.clearColor(0.4F, 0.6F, 0.9F); // sky color
        final PerspectiveCamera camera = super.context.player().camera();
        camera.resize(Jpize.getWidth(), Jpize.getHeight());
        super.context.player().input().enable();
    }

    public static boolean AO = true;

    @Override
    public void update() {
        // exit to main_menu screen
        if(Key.ESCAPE.down()){
            super.context.disconnectSession();
            super.context.screens().setCurrent(ScreenMainMenu.SCREEN_ID);
        }

        // tesselate chunk meshes
        super.context.level().update();

        // player
        final ClientPlayer player = super.context.player();
        player.update();

        // select block to place
        if(Key.NUM_1.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("stone").resource().getDefaultState();
        }else if(Key.NUM_2.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("grass_block").resource().getDefaultState();
        }else if(Key.NUM_3.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("dirt").resource().getDefaultState();
        }else if(Key.NUM_4.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("stone_stairs").resource().getDefaultState();
        }else if(Key.NUM_5.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("torch").resource().getDefaultState();
        }else if(Key.NUM_6.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("wall_torch").resource().getDefaultState();
        }else if(Key.NUM_7.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("redstone_torch").resource().getDefaultState();
        }else if(Key.NUM_8.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("redstone_wall_torch").resource().getDefaultState();
        }else if(Key.NUM_9.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("red_shroom_0").resource().getDefaultState();
        }else if(Key.NUM_0.down()) {
            toPlaceBlockState = ServerRegistries.BLOCK.get("campfire").resource().getDefaultState();
        }

        if(Key.F5.down()) {
            AO = true;
        }else if(Key.F6.down()) {
            AO = false;
        }

        final BlockSelectRay ray = player.blockSelectRay();
        if(ray.hasSelection()){
            if(MouseBtn.LEFT.down() && MouseBtn.RIGHT.down()) {
                // replace
                final Vec3i position = ray.getDestroyPosition();
                super.context.level().setBlockState(position.x, position.y, position.z, Block.AIR.getDefaultState());

                // facing
                Facing facing = Facing.byAngle(player.camera().rotation().getYaw());
                if(toPlaceBlockState.isBlockID("stone_stairs"))
                    facing = facing.opposite();

                // blockstate properties
                toPlaceBlockState = toPlaceBlockState.withProperty(StateProperty.BITES, Maths.random(0, 4));
                toPlaceBlockState = toPlaceBlockState.withProperty(StateProperty.SHAPE, "straight");
                toPlaceBlockState = toPlaceBlockState.withProperty(StateProperty.FACING, facing);
                toPlaceBlockState = toPlaceBlockState.withProperty(StateProperty.LIT, true);

                super.context.level().setBlockState(position.x, position.y, position.z, toPlaceBlockState);

            }else if(MouseBtn.LEFT.down()){
                // destroy
                final Vec3i position = ray.getDestroyPosition();
                super.context.level().setBlockState(position.x, position.y, position.z, Block.AIR.getDefaultState());

            }else if(MouseBtn.RIGHT.down()){
                // place

                // facing
                Facing facing = Facing.byAngle(player.camera().rotation().getYaw());
                if(toPlaceBlockState.isBlockID("stone_stairs"))
                    facing = facing.opposite();

                // blockstate properties
                toPlaceBlockState = toPlaceBlockState.withProperty(StateProperty.BITES, Maths.random(0, 4));
                toPlaceBlockState = toPlaceBlockState.withProperty(StateProperty.SHAPE, "straight");
                toPlaceBlockState = toPlaceBlockState.withProperty(StateProperty.FACING, facing);
                toPlaceBlockState = toPlaceBlockState.withProperty(StateProperty.LIT, true);

                final Vec3i position = ray.getPlacePosition();
                super.context.level().setBlockState(position.x, position.y, position.z, toPlaceBlockState);

            }else if(MouseBtn.MIDDLE.down()){
                // pick
                final Vec3i position = ray.getDestroyPosition();
                toPlaceBlockState = super.context.level().getBlockState(position.x, position.y, position.z);
            }
        }
    }

    @Override
    public void render() {
        // -res-
        final Font font = ClientResources.FONTS.get("default").resource();
        final FontRenderOptions fontOptions = font.getOptions();

        // level
        Gl.enable(GlTarget.DEPTH_TEST);
        final PerspectiveCamera camera = super.context.player().camera();
        super.context.level().render(camera);

        // entities

        final Vec2f prevScale = fontOptions.scale().copy();
        fontOptions.scale().set(0.05F);
        for(AbstractEntity entity : super.context.entities()){
            final Vec3f pos = entity.position();

            float angleY = Vec2f.angle(camera.getX() - pos.x, camera.getZ() - pos.z) + 90;
            float angleX = Vec2f.angleBetween(Vec2f.len(camera.getX() - pos.x, camera.getZ() - pos.z), camera.getY() - pos.y, 0, 1) - 90;
            float angleZ = camera.rotation().getYaw();

            final String text = entity.getDisplayName();
            final Vec2f bounds = font.getTextBounds(text);

            fontOptions.matrix().setRotationXYZ(angleX, angleY, 0);
            font.drawText(camera, text, pos.x - bounds.x * 0.5F, pos.y - bounds.y * 0.5F, pos.z);
        }
        fontOptions.scale().set(prevScale);

        // hud
        Gl.disable(GlTarget.DEPTH_TEST);
        batch.setup();

        // crosshair
        final float crosshairSize = Jpize.getHeight() * 0.03F;
        final float crosshairX = (Jpize.getWidth() - crosshairSize) * 0.5F;
        final float crosshairY = (Jpize.getHeight() - crosshairSize) * 0.5F;
        batch.draw(crosshair, crosshairX, crosshairY, crosshairSize, crosshairSize);

        // place block ID
        final String selectedBlockText = ("Selected block: " + toPlaceBlockState.getBlockID());
        final Vec2f selectedBlockTextBounds = font.getTextBounds(selectedBlockText);
        final float selectedBlockTextX = (Jpize.getWidth() - selectedBlockTextBounds.x);
        final float selectedBlockTextY = (Jpize.getHeight() - 10F);
        font.drawText(batch, selectedBlockText, selectedBlockTextX, selectedBlockTextY);

        // place block ID
        final BlockSelectRay ray = super.context.player().blockSelectRay();
        if(ray.hasSelection()){
            final StateProperty<?>[] keys = ray.getBlockState().getStateProperties().keySet().toArray(new StateProperty[0]);
            final AtomicInteger i = new AtomicInteger();
            final String aimBlockstateText = (
                "Blockstate: '" + ray.getBlockState().getBlockID() +
                "' " + new StringList(ray.getBlockState().getStateProperties().values(), (v) -> keys[i.getAndIncrement()].getName() + "=" + v
            ));
            final Vec2f aimBlockstateTextBounds = font.getTextBounds(aimBlockstateText);
            final float aimBlockstateTextX = (Jpize.getWidth() - aimBlockstateTextBounds.x);
            final float aimBlockstateTextY = (Jpize.getHeight() - 10F - selectedBlockTextBounds.y);
            font.drawText(batch, aimBlockstateText, aimBlockstateTextX, aimBlockstateTextY);
        }

        batch.render(false);
    }

    @Override
    public void resize(int width, int height) {
        final PerspectiveCamera camera = super.context.player().camera();
        camera.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

}