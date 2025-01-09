package generaloss.mc24.client.block;

import generaloss.mc24.server.Direction;
import generaloss.mc24.server.block.BlockState;
import jpize.util.math.Intersector;
import jpize.util.math.vector.Vec2f;


import java.util.*;

public class BlockModel {

    private final BlockState blockstate;
    private final Map<Direction, List<BlockFace>> faceGroups;
    private final List<Direction> hidesOthersFaceList;
    private boolean hasAmbientOcclusion;

    public BlockModel(BlockState blockstate) {
        this.blockstate = blockstate;
        this.faceGroups = new HashMap<>();
        this.hidesOthersFaceList = new ArrayList<>();
        this.setHasAmbientOcclusion(true);
    }

    public BlockState getBlockState() {
        return blockstate;
    }


    public List<BlockFace> getFacesGroup(Direction dir) {
        return faceGroups.getOrDefault(dir, List.of());
    }

    public BlockModel addFace(BlockFace face) {
        // add to group
        final Direction direction = face.getDirection();
        final List<BlockFace> group = faceGroups.getOrDefault(direction, new ArrayList<>());
        group.add(face);
        faceGroups.put(direction, group);
        // hide opposite
        final Direction oppositeDirection = direction.opposite();
        if(!hidesOthersFaceList.contains(oppositeDirection) && (face.isSolid() || this.isMultiFaceSolid(direction)))
            hidesOthersFaceList.add(oppositeDirection);
        return this;
    }


    private boolean isMultiFaceSolid(Direction dir) {
        final List<BlockFace> faces = faceGroups.get(dir);
        if(faces == null)
            return false;
        for(BlockFace face1 : faces) {
            for(BlockFace face2 : faces){
                if(face1 == face2)
                    continue;
                if(isQuadsIntersects(face1, face2))
                    return false;
            }
        }
        return false;
    }

    private static boolean isQuadsIntersects(BlockFace quad1, BlockFace quad2) {
        return false;
    }


    public boolean isHidesOthersFace(Direction dir) {
        return hidesOthersFaceList.contains(dir);
    }


    public boolean hasAmbientOcclusion() {
        return hasAmbientOcclusion;
    }

    public BlockModel setHasAmbientOcclusion(boolean hasAmbientOcclusion) {
        this.hasAmbientOcclusion = hasAmbientOcclusion;
        return this;
    }

}
