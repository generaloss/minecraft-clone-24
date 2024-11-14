package generaloss.mc24.client.screen;

import generaloss.mc24.client.Main;
import jpize.app.Jpize;
import jpize.util.Disposable;

import java.util.HashMap;
import java.util.Map;

public class ScreenDispatcher implements Disposable {

    private final Map<String, IScreen> map;
    private IScreen current;

    public ScreenDispatcher() {
        this.map = new HashMap<>();
    }


    public ScreenDispatcher register(IScreen... screens) {
        for(IScreen screen: screens)
            map.put(screen.getID(), screen);
        return this;
    }


    public Map<String, IScreen> getScreens() {
        return map;
    }

    public IScreen getCurrent() {
        return current;
    }

    public boolean isCurrent(IScreen screen) {
        return current == screen;
    }

    public boolean isCurrent(String ID) {
        final IScreen screen = map.get(ID);
        return (screen != null && isCurrent(screen));
    }


    public void update() {
        if(current != null)
            current.update();
    }

    public void render() {
        if(current != null)
            current.render();
    }


    public ScreenDispatcher show(String ID) {
        final IScreen screen = map.get(ID);
        if(screen == null)
            return this;

        if(current != null)
            current.hide();
        current = screen;

        if(!screen.isInitialized()){
            screen.setInitialized();
            screen.init();
        }
        screen.show();
        screen.resize(Jpize.getWidth(), Jpize.getHeight());
        return this;
    }

    public void hide() {
        if(current != null){
            current.hide();
            current = null;
        }
    }


    public void resize(int width, int height) {
        if(current != null)
            current.resize(width, height);
    }


    @Override
    public void dispose() {
        this.hide();
        for(IScreen screen: map.values())
            screen.dispose();
    }

}
