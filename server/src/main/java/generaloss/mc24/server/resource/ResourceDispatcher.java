package generaloss.mc24.server.resource;

import jpize.util.Disposable;
import jpize.util.time.Stopwatch;

import java.util.HashMap;
import java.util.Map;

public class ResourceDispatcher implements Disposable {

    private final Map<String, ResourceHandle<?>> resources;
    private String directory;

    public ResourceDispatcher() {
        this.resources = new HashMap<>();
        this.resetDirectory();
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getDefaultDirectory() {
        return "assets/server";
    }

    public void resetDirectory() {
        this.setDirectory(this.getDefaultDirectory());
    }


    public <T> ResourceHandle<T> register(ResourceHandle<T> resourceHandle) {
        resources.put(resourceHandle.getIdentifier(), resourceHandle);
        return resourceHandle;
    }

    public ResourceBlock registerBlock(String identifier, String path) {
        return (ResourceBlock) this.register(new ResourceBlock(this, identifier, path));
    }


    public <T extends ResourceHandle<?>> T get(String identifier) {
        // noinspection unchecked
        return (T) resources.get(identifier);
    }

    public void reloadAll() {
        final Stopwatch stopwatch = new Stopwatch().start();
        for(ResourceHandle<?> resource: resources.values())
            resource.reload();
        System.out.println("Reloaded " + resources.size() + " resources (" + stopwatch.getMillis() + " ms)");
    }


    @Override
    public void dispose() {
        for(ResourceHandle<?> resource: resources.values())
            resource.dispose();
    }

}
