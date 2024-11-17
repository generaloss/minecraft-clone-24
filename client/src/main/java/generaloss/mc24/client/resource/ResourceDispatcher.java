package generaloss.mc24.client.resource;

import jpize.util.Disposable;
import jpize.util.time.Stopwatch;

import java.util.HashMap;
import java.util.Map;

public class ResourceDispatcher implements Disposable {

    public static final String DEFAULT_ROOT_DIR = "assets/vanilla-pack";

    private final Map<String, ResourceHandle<?>> resources;
    private String rootDirectory;

    public ResourceDispatcher() {
        this.resources = new HashMap<>();
        this.setDefaultRootDirectory();
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void setDefaultRootDirectory() {
        this.rootDirectory = DEFAULT_ROOT_DIR;
    }


    public <T extends Disposable> ResourceHandle<T> register(ResourceHandle<T> resourceHandle) {
        resources.put(resourceHandle.getIdentifier(), resourceHandle);
        return resourceHandle;
    }

    public ResourceTexture registerTexture(String identifier, String path) {
        return (ResourceTexture) this.register(new ResourceTexture(this, identifier, path));
    }

    public ResourceSkybox registerSkybox(String identifier, String path) {
        return (ResourceSkybox) this.register(new ResourceSkybox(this, identifier, path));
    }

    public ResourceMusic registerMusic(String identifier, String path) {
        return (ResourceMusic) this.register(new ResourceMusic(this, identifier, path));
    }

    public ResourceShader registerShader(String identifier, String path) {
        return (ResourceShader) this.register(new ResourceShader(this, identifier, path));
    }

    public ResourceAtlas registerAtlas(String identifier, String path, int width, int height) {
        return (ResourceAtlas) this.register(new ResourceAtlas(this, identifier, path, width, height));
    }


    public <T extends Disposable> T get(String identifier) {
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
