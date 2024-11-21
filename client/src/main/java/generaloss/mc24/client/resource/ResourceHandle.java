package generaloss.mc24.client.resource;

import jpize.util.Disposable;

public abstract class ResourceHandle<T> implements Disposable {

    private final ResourceDispatcher dispatcher;
    private final String identifier;
    private String path;

    protected ResourceHandle(ResourceDispatcher dispatcher, String identifier, String path) {
        this.dispatcher = dispatcher;
        this.identifier = identifier;
        this.setPath(path);
    }

    public ResourceDispatcher dispatcher() {
        return dispatcher;
    }

    public String getIdentifier() {
        return identifier;
    }

    public abstract T resource();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public abstract void reload();

}
