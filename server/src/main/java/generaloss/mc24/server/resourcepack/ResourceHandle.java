package generaloss.mc24.server.resourcepack;

import generaloss.mc24.server.Identifiable;
import jpize.util.Disposable;
import jpize.util.res.Resource;
import java.util.Collection;
import java.util.Iterator;

public abstract class ResourceHandle<Key, Object> implements Identifiable<Key>, Disposable {

    protected final String path;

    public ResourceHandle(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    abstract public Key getID();

    abstract public Object getObject();

    abstract public void load(ResourcePack defaultPack);

    abstract public void reload(Collection<ResourcePack> packs);

    @Override
    abstract public void dispose();

    protected <R extends Resource> R getResourceFromPacks(Collection<ResourcePack> packs, String path) {
        R resource = null;
        final Iterator<ResourcePack> packIterator = packs.iterator();
        while(resource == null)
            resource = (R) packIterator.next().getResource(path);
        return resource;
    }

}