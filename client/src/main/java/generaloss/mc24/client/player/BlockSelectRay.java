package generaloss.mc24.client.player;

import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.server.Directory;
import generaloss.mc24.server.block.BlockState;
import jpize.util.camera.Camera3D;
import jpize.util.math.Mathc;
import jpize.util.math.Maths;
import jpize.util.math.vector.Vec3f;
import jpize.util.math.vector.Vec3i;

public class BlockSelectRay {

    private boolean hasSelection;
    private Directory selectedFace;
    private final Vec3i destroyPosition;
    private final Vec3i placePosition;
    private BlockState blockstate;

    public BlockSelectRay() {
        this.destroyPosition = new Vec3i();
        this.placePosition = new Vec3i();
    }


    public boolean hasSelection() {
        return hasSelection;
    }

    public Directory getSelectedFace() {
        return selectedFace;
    }

    public Vec3i getDestroyPosition() {
        return destroyPosition;
    }

    public Vec3i getPlacePosition() {
        return placePosition;
    }

    public BlockState getBlockState() {
        return blockstate;
    }


    public void update(Camera3D camera, WorldLevel level, float distance) {
        final Vec3f pos = camera.position();
        final Vec3f dir = camera.getDirection();

        final Vec3i step = new Vec3i(Mathc.signum(dir.x), Mathc.signum(dir.y), Mathc.signum(dir.z));
        final Vec3f delta = new Vec3f(step.x / dir.x, step.y / dir.y, step.z / dir.z);
        final Vec3f tMax = new Vec3f(Math.min(distance / 2, Math.abs((Math.max(step.x, 0) - Maths.frac(pos.x)) / dir.x)), Math.min(distance / 2, Math.abs((Math.max(step.y, 0) - Maths.frac(pos.y)) / dir.y)), Math.min(distance / 2, Math.abs((Math.max(step.z, 0) - Maths.frac(pos.z)) / dir.z)));

        destroyPosition.set(pos.xFloor(), pos.yFloor(), pos.zFloor());
        final Vec3i faceNormal = new Vec3i();

        hasSelection = false;
        while(tMax.len() < distance){

            if(tMax.x < tMax.y){
                if(tMax.x < tMax.z){
                    tMax.x += delta.x;
                    destroyPosition.x += step.x;
                    faceNormal.set(-step.x, 0, 0);
                }else{
                    tMax.z += delta.z;
                    destroyPosition.z += step.z;
                    faceNormal.set(0, 0, -step.z);
                }
            }else{
                if(tMax.y < tMax.z){
                    tMax.y += delta.y;
                    destroyPosition.y += step.y;
                    faceNormal.set(0, -step.y, 0);
                }else{
                    tMax.z += delta.z;
                    destroyPosition.z += step.z;
                    faceNormal.set(0, 0, -step.z);
                }
            }

            final BlockState blockstate = level.getBlockState(destroyPosition.x, destroyPosition.y, destroyPosition.z);

            if(!blockstate.isBlockID("void", "air")){
                this.hasSelection = true;
                this.selectedFace = Directory.byVector(faceNormal.x, faceNormal.y, faceNormal.z);
                this.placePosition.set(destroyPosition).add(selectedFace.getNormal());
                this.blockstate = blockstate;
                break;

                // if(blockstate.getCursor() == BlockCursor.SOLID){
                // }else{
                //     final BlockCursor shape = blockstate.getCursor();
                //     blockMatrix.toTranslated(selectedBlock);
                //     if(Intersector.isRayIntersectQuadMesh(ray, blockMatrix, shape.getVertices(), shape.getQuadIndices())){
                //         selectedFace = Dir.fromNormal(faceNormal.x, faceNormal.y, faceNormal.z);
                //         selectedBlockProps = blockstate;
                //         imaginarySelectedBlock.set(selectedBlock).add(selectedFace.getNormal());
                //         selected = true;

                //         break;
                //     }
                // }
            }
        }
    }

}
