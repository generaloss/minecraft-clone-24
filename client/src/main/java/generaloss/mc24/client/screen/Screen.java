package generaloss.mc24.client.screen;

import generaloss.mc24.client.Main;
import jpize.util.screen.AbstractScreen;

public abstract class Screen extends AbstractScreen<String> {

    public final Main context;

    public Screen(Main context, String ID) {
        this.context = context;
    }

}
