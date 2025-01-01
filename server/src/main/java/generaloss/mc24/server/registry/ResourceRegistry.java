package generaloss.mc24.server.registry;

import generaloss.mc24.server.resourcepack.ResourceHandle;
import generaloss.mc24.server.resourcepack.ResourcePack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ResourceRegistry <ID, Resource extends ResourceHandle<ID, ?>> implements Iterable<Resource> {

    private final Queue<Resource> toLoad;
    private final Map<ID, Resource> map;

    public ResourceRegistry() {
        this.toLoad = new ConcurrentLinkedQueue<>();
        this.map = new HashMap<>();
    }

    public Resource registerResource(Resource object) {
        toLoad.add(object);
        return object;
    }

    public Resource getResource(ID ID) {
        return map.get(ID);
    }

    public <O> O getObject(ID ID) {
        return (O) this.getResource(ID).getObject();
    }

    public void load(ResourcePack defaultPack) {
        while(!toLoad.isEmpty()) {
            final Resource resource = toLoad.poll();
            resource.load(defaultPack);
            map.put(resource.getID(), resource);
        }
    }

    public void reload(Collection<ResourcePack> packs) {
        for(Resource resource : map.values())
            resource.reload(packs);
    }

    public void dispose() {
        for(Resource resource : map.values())
            resource.dispose();
    }

    public Collection<Resource> getResourcesToLoad() {
        return toLoad;
    }

    public Collection<Resource> getLoadedResources() {
        return map.values();
    }

    public int size() {
        return Math.max(map.size(), toLoad.size());
    }

    @Override
    public @NotNull Iterator<Resource> iterator() {
        return map.values().iterator();
    }
}