package generaloss.mc24.client.resourcepack;

import generaloss.mc24.server.resourcepack.ResourceHandle;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.gl.shader.Shader;
import jpize.util.res.Resource;

public class ResourceShader extends ResourceHandle<String, Shader> {

    private final Shader shader;

    public ResourceShader(ResourcePack defaultPack, String ID, String path) {
        super(defaultPack, ID, path);
        this.shader = new Shader();
    }

    @Override
    public Shader object() {
        return shader;
    }

    @Override
    public void load(ResourcePack pack) {
        final Resource vertexRes = pack.getOrDefault(super.getPath() + ".vsh", super.getDefaultPack());
        final Resource fragmentRes = pack.getOrDefault(super.getPath() + ".fsh", super.getDefaultPack());
        shader.load(vertexRes, fragmentRes);
    }

    @Override
    public void dispose() {
        shader.dispose();
    }

}
