package generaloss.mc24.client.resource;

import generaloss.mc24.server.registry.Identifiable;
import jpize.util.Disposable;

public abstract class ResourceHandle<Object> implements Disposable, Identifiable<String> {

    private final String ID;
    private String path;

    protected ResourceHandle(String ID, String path) {
        this.ID = ID;
        this.setPath(path);
    }

    @Override
    public String getID() {
        return ID;
    }

    public abstract Object object();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public abstract void reload();

}
