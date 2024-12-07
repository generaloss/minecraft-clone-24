package generaloss.mc24.client.registry;

import generaloss.mc24.client.resourcepack.ResourceSkybox;
import generaloss.mc24.server.registry.ResourceRegistry;
import jpize.gl.texture.Skybox;

public class RegistrySkyboxes extends ResourceRegistry<String, ResourceSkybox> {

    public Skybox get(String ID) {
        return super.getResource(ID).getObject();
    }

    public ResourceSkybox register(String ID, String path) {
        return super.registerResource(new ResourceSkybox(ID, path));
    }

}
