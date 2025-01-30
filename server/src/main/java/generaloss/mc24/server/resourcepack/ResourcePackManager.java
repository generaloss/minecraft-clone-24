package generaloss.mc24.server.resourcepack;

import jpize.util.res.Resource;
import jpize.util.res.IResourceSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ResourcePackManager implements IResourceSource {

    private final ResourcePack corePack;
    private final List<ResourcePack> packs;

    public ResourcePackManager() {
        this.corePack = new ResourcePack("core-pack.zip");
        this.packs = new ArrayList<>();
    }

    public ResourcePack getCorePack() {
        return corePack;
    }

    public Collection<ResourcePack> getActivePacks() {
        final List<ResourcePack> list = new ArrayList<>(packs);
        list.add(corePack);
        return list;
    }


    public ResourcePackManager putPack(ResourcePack pack) {
        packs.add(pack);
        return this;
    }

    public ResourcePackManager putPack(int i, ResourcePack pack) {
        packs.add(i, pack);
        return this;
    }


    public ResourcePackManager removePack(ResourcePack pack) {
        packs.remove(pack);
        return this;
    }

    public ResourcePackManager removePack(int i) {
        packs.remove(i);
        return this;
    }

    public ResourcePackManager clear() {
        packs.clear();
        return this;
    }


    @Override
    public Resource getResource(String path) {
        final Iterator<ResourcePack> packIterator = packs.iterator();

        Resource resource = null;
        while(packIterator.hasNext() && resource == null)
            resource = packIterator.next().getResource(path);

        if(resource == null)
            resource = corePack.getResource(path);

        return resource;
    }

}
