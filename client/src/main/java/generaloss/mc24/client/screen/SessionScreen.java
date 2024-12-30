package generaloss.mc24.client.screen;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.client.player.ClientPlayer;
import generaloss.mc24.server.Directory;
import generaloss.mc24.server.block.BlockState;
import jpize.gl.Gl;
import jpize.gl.glenum.GlTarget;
import jpize.glfw.input.Key;
import jpize.glfw.input.MouseBtn;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.math.Mathc;
import jpize.util.math.Maths;
import jpize.util.math.Ray3f;
import jpize.util.math.vector.Vec3f;
import jpize.util.math.vector.Vec3i;

public class SessionScreen extends IScreen {

    public SessionScreen(Main context) {
        super(context, "session");
    }

    @Override
    public void show() {
        Gl.clearColor(0.25F, 0.35F, 0.5F); // blue sky color ingame
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

        // place block
        if(MouseBtn.LEFT.down() || MouseBtn.RIGHT.down()) {
            final Ray3f ray = new Ray3f();
            final float length = 100;
            boolean selected;
            final Vec3i selectedBlock = new Vec3i();
            final Vec3i imaginarySelectedBlock = new Vec3i();
            final Directory selectedFace;
            final WorldLevel level = super.context().level();
            BlockState selectedBlockState;

            final ClientPlayer player = super.context().player();
            ray.dir().set(player.camera().getDirection());
            ray.origin().set(player.camera().position()).copy().add(0, 0.0F, 0);

            // Get pos, dir, len
            final Vec3f pos = ray.origin();
            final Vec3f dir = ray.dir();

            // ...
            final Vec3i step = new Vec3i(
                Mathc.signum(dir.x),
                Mathc.signum(dir.y),
                Mathc.signum(dir.z)
            );
            final Vec3f delta = new Vec3f(
                step.x / dir.x,
                step.y / dir.y,
                step.z / dir.z
            );
            final Vec3f tMax = new Vec3f(
                Math.min(length / 2, Math.abs((Math.max(step.x, 0) - Maths.frac(pos.x)) / dir.x)),
                Math.min(length / 2, Math.abs((Math.max(step.y, 0) - Maths.frac(pos.y)) / dir.y)),
                Math.min(length / 2, Math.abs((Math.max(step.z, 0) - Maths.frac(pos.z)) / dir.z))
            );

            selectedBlock.set(pos.xFloor(), pos.yFloor(), pos.zFloor());
            final Vec3i faceNormal = new Vec3i();

            selected = false;
            while(tMax.len() < length){

                if(tMax.x < tMax.y){
                    if(tMax.x < tMax.z){
                        tMax.x += delta.x;
                        selectedBlock.x += step.x;
                        faceNormal.set(-step.x, 0, 0);
                    }else{
                        tMax.z += delta.z;
                        selectedBlock.z += step.z;
                        faceNormal.set(0, 0, -step.z);
                    }
                }else{
                    if(tMax.y < tMax.z){
                        tMax.y += delta.y;
                        selectedBlock.y += step.y;
                        faceNormal.set(0, -step.y, 0);
                    }else{
                        tMax.z += delta.z;
                        selectedBlock.z += step.z;
                        faceNormal.set(0, 0, -step.z);
                    }
                }

                final BlockState blockState = level.getBlockState(selectedBlock.x, selectedBlock.y, selectedBlock.z);

                if(blockState != null && !blockState.isBlockID("air")){
                    // if(blockState.getCursor() == BlockCursor.SOLID){
                        selectedFace = Directory.byVector(faceNormal.x, faceNormal.y, faceNormal.z);
                        selectedBlockState = blockState;
                        imaginarySelectedBlock.set(selectedBlock).add(selectedFace.getNormal());
                        selected = true;

                        break;
                    // }else{
                    //     final BlockCursor shape = blockState.getCursor();
                    //     blockMatrix.toTranslated(selectedBlock);
                    //     if(Intersector.isRayIntersectQuadMesh(ray, blockMatrix, shape.getVertices(), shape.getQuadIndices())){
                    //         selectedFace = Dir.fromNormal(faceNormal.x, faceNormal.y, faceNormal.z);
                    //         selectedBlockProps = blockState;
                    //         imaginarySelectedBlock.set(selectedBlock).add(selectedFace.getNormal());
                    //         selected = true;

                    //         break;
                    //     }
                    // }
                }
            }

            if(selected){
                if(MouseBtn.LEFT.down()){
                    super.context().level()
                        .setBlockState(selectedBlock.x, selectedBlock.y, selectedBlock.z, super.context()
                            .registries().BLOCKS.get("air").getDefaultState());
                }else if(MouseBtn.RIGHT.down()){
                    super.context().level()
                        .setBlockState(imaginarySelectedBlock.x, imaginarySelectedBlock.y, imaginarySelectedBlock.z, super.context()
                            .registries().BLOCKS.get(currentBlockID).getDefaultState());
                }
            }
        }

        if(Key.NUM_1.down())
            currentBlockID = "stone";
        else if(Key.NUM_2.down())
            currentBlockID = "grass_block";
        else if(Key.NUM_3.down())
            currentBlockID = "dirt";
        else if(Key.NUM_4.down())
            currentBlockID = "torch";
        else if(Key.NUM_5.down())
            currentBlockID = "blue_torch";
        else if(Key.NUM_6.down())
            currentBlockID = "stairs";
    }

    String currentBlockID = "stone";

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