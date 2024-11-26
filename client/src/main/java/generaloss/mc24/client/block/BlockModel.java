package generaloss.mc24.client.block;

import generaloss.mc24.server.Directory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockModel {

    private boolean dontHidesSameBlockFaces;
    private final Map<Directory, List<BlockFace>> faceGroups;
    private final Map<Directory, Boolean> hideOppositeFacesMap;

    public BlockModel() {
        this.faceGroups = new HashMap<>();
        this.hideOppositeFacesMap = new HashMap<>();
    }

    public BlockModel addFace(BlockFace face) {
        // add face
        final Directory dir = face.getHidesFace().opposite();
        final List<BlockFace> group = faceGroups.getOrDefault(dir, new ArrayList<>());
        group.add(face);
        faceGroups.put(dir, group);
        // hide faces map
        final Directory hideOppositeFace = face.getHideOppositeFace();
        if(hideOppositeFace != Directory.NONE)
            hideOppositeFacesMap.put(hideOppositeFace, true);
        return this;
    }

    public void clear() {
        faceGroups.clear();
        hideOppositeFacesMap.clear();
    }

    public List<BlockFace> getFacesGroup(Directory dir) {
        return faceGroups.getOrDefault(dir, List.of());
    }
    
    public boolean isHidesOppositeFace(Directory dir) {
        return hideOppositeFacesMap.containsKey(dir);
    }

    public boolean isNotHidesOppositeFace(Directory dir) {
        return !this.isHidesOppositeFace(dir);
    }

    public boolean isDontHidesSameBlockFaces() {
        return dontHidesSameBlockFaces;
    }

    public BlockModel setDontHidesSameBlockFaces(boolean dontHideSameBlockFaces) {
        this.dontHidesSameBlockFaces = dontHideSameBlockFaces;
        return this;
    }

}
