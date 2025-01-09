package generaloss.mc24.server.resourcepack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResourcePackManager {

    private final ResourcePack corePack;
    private final List<ResourcePack> activePacks;

    public ResourcePackManager() {
        this.corePack = new ResourcePack("core-pack.zip");
        this.activePacks = new ArrayList<>();
    }

    public ResourcePack getCorePack() {
        return corePack;
    }

    public Collection<ResourcePack> getActivePacks() {
        final List<ResourcePack> packs = new ArrayList<>(activePacks);
        packs.add(corePack);
        for(ResourcePack pack: packs){
            System.out.println(pack.getID());
        }
        return packs;
    }


    public void putPack(ResourcePack pack) {
        activePacks.add(pack);
    }

    public void putPack(String packFilename) {
        this.putPack(new ResourcePack(packFilename));
    }

    public void putPack(int i, ResourcePack pack) {
        activePacks.add(i, pack);
    }

    public void putPack(int i, String packFilename) {
        this.putPack(i, new ResourcePack(packFilename));
    }


    public void removePack(ResourcePack pack) {
        activePacks.remove(pack);
    }

    public void removePack(int i) {
        activePacks.remove(i);
    }


    public void clear() {
        activePacks.clear();
    }

}
