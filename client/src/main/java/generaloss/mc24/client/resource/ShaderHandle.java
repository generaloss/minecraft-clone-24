package generaloss.mc24.client.resource;

import jpize.gl.shader.Shader;
import jpize.util.res.Resource;
import jpize.util.res.ResourceSource;
import jpize.util.res.handle.ResHandle;

public class ShaderHandle extends ResHandle<String, Shader> {

    private final Shader shader;

    public ShaderHandle(String key, String path) {
        super(key, path);
        this.shader = new Shader();
    }


    @Override
    public Shader resource() {
        return shader;
    }

    @Override
    public void load(ResourceSource source, String path) {
        final Resource vertexRes = source.getResource(path + ".vsh");
        final Resource fragmentRes = source.getResource(path + ".fsh");
        shader.load(vertexRes, fragmentRes);
    }

    @Override
    public void dispose() {
        shader.dispose();
    }

}
