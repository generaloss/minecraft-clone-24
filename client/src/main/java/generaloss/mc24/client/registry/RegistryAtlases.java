package generaloss.mc24.client.registry;

import generaloss.mc24.client.resourcepack.ResourceAtlas;
import generaloss.mc24.server.registry.ResourceRegistry;
import jpize.util.atlas.TextureAtlas;

public class RegistryAtlases extends ResourceRegistry<String, ResourceAtlas> {

    public TextureAtlas<String> get(String ID) {
        return super.getResource(ID).getObject();
    }

    public ResourceAtlas register(String ID, String path, int width, int height) {
        return super.registerResource(new ResourceAtlas(ID, path, width, height));
    }

}
