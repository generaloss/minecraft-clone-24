package generaloss.mc24.client.render;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.session.SessionScreen;

public class SessionRenderers {

    private final LevelRenderer level;

    public SessionRenderers(Main context, SessionScreen session) {
        this.level = new LevelRenderer(context, session, session.level());
    }

    public void render() {
        level.render();
    }

}
