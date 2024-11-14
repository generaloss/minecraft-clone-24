package generaloss.mc24.client.screen;

import generaloss.mc24.client.Main;
import jpize.util.Disposable;

public abstract class IScreen implements Disposable {

    private final Main context;
    private final String ID;
    private boolean initialized;

    public IScreen(Main context, String ID) {
        this.context = context;
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    protected boolean isInitialized() {
        return initialized;
    }

    protected void setInitialized() {
        this.initialized = true;
    }

    public Main context() {
        return context;
    }


    public void init() { }

    public void update() { }

    public void render() { }

    public void show() { }

    public void hide() { }

    public void resize(int width, int height) { }

    @Override
    public void dispose() { }

}
