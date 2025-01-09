package generaloss.mc24.client.block;

import generaloss.mc24.server.Direction;
import generaloss.mc24.server.block.BlockState;


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
        if(face.isSolid())
            hidesOthersFaceList.add(face.getDirection().opposite());
        return this;
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
