package generaloss.mc24.client.session;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.screen.IScreen;

public class SessionScreen extends IScreen {

    private final ClientSession session;

    public SessionScreen(Main context, ClientSession session) {
        super(context, "session");
        this.session = session;
    }

    public ClientSession session() {
        return session;
    }

    @Override
    public void show() {
        session.connect(null, 0);
    }

    @Override
    public void hide() {
        session.close();
    }

    @Override
    public void update() {
        session.update();
    }

    @Override
    public void render() {
        session.render();
    }

    @Override
    public void resize(int width, int height) {
        session.resize(width, height);
    }

}