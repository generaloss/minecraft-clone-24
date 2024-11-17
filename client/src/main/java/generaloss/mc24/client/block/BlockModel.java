package generaloss.mc24.client.block;

import generaloss.mc24.server.common.Directory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockModel {

    private final boolean dontOccludeSameBlock;
    private final Map<Directory, List<BlockFace>> faceGroups;
    private final Map<Directory, Boolean> occludeMap;

    public BlockModel(boolean dontOccludeSameBlock) {
        this.dontOccludeSameBlock = dontOccludeSameBlock;
        this.faceGroups = new HashMap<>();
        this.occludeMap = new HashMap<>();
    }

    public BlockModel addFace(BlockFace face) {
        // add face
        final Directory dir = face.getOccludesTo().opposite();
        final List<BlockFace> group = faceGroups.getOrDefault(dir, new ArrayList<>());
        group.add(face);
        faceGroups.put(dir, group);
        // occlusions
        final Directory occlude = face.getOcclude();
        if(occlude != Directory.NONE)
            occludeMap.put(occlude, true);
        return this;
    }

    public List<BlockFace> getFacesGroup(Directory dir) {
        return faceGroups.getOrDefault(dir, List.of());
    }
    
    public boolean isOcclude(Directory dir) {
        return occludeMap.containsKey(dir);
    }

    public boolean isDontOccludeSameBlock() {
        return dontOccludeSameBlock;
    }

}
