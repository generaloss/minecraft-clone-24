package generaloss.mc24.server.resourcepack;

import generaloss.mc24.server.registry.Identifiable;
import jpize.util.Disposable;

public abstract class ResourceHandle<Key, Object> implements Disposable, Identifiable<Key> {

    private final ResourcePack defaultPack;
    private Key ID;
    private String path;

    protected ResourceHandle(ResourcePack defaultPack, Key ID, String path) {
        this.defaultPack = defaultPack;
        this.ID = ID;
        this.setPath(path);
    }

    protected ResourceHandle(ResourcePack defaultPack, String path) {
        this.defaultPack = defaultPack;
        this.setPath(path);
    }

    public ResourcePack getDefaultPack() {
        return defaultPack;
    }

    protected void setID(Key ID) {
        this.ID = ID;
    }

    @Override
    public Key getID() {
        return ID;
    }

    public abstract Object object();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public abstract void load(ResourcePack pack);

}
