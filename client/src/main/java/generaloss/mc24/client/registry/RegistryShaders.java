package generaloss.mc24.client.registry;

import generaloss.mc24.client.resourcepack.ResourceShader;
import generaloss.mc24.server.registry.ResourceRegistry;
import jpize.gl.shader.Shader;

public class RegistryShaders extends ResourceRegistry<String, ResourceShader> {

    public Shader get(String ID) {
        return super.getResource(ID).getObject();
    }

    public ResourceShader register(String ID, String path) {
        return super.registerResource(new ResourceShader(ID, path));
    }

}
