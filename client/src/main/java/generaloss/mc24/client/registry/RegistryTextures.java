package generaloss.mc24.client.registry;

import generaloss.mc24.client.resourcepack.ResourceTexture;
import generaloss.mc24.server.registry.ResourceRegistry;
import jpize.gl.texture.Texture2D;

public class RegistryTextures extends ResourceRegistry<String, ResourceTexture> {

    public Texture2D get(String ID) {
        return super.getResource(ID).getObject();
    }

    public ResourceTexture register(String ID, String path) {
        return super.registerResource(new ResourceTexture(ID, path));
    }

}
