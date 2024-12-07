package generaloss.mc24.client.registry;

import generaloss.mc24.client.resourcepack.ResourceFont;
import generaloss.mc24.server.registry.ResourceRegistry;
import jpize.util.font.Font;

public class RegistryFonts extends ResourceRegistry<String, ResourceFont> {

    public Font get(String ID) {
        return super.getResource(ID).getObject();
    }

    public ResourceFont register(String ID, String path) {
        return super.registerResource(new ResourceFont(ID, path));
    }

}
