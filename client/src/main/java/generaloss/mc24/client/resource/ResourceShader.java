package generaloss.mc24.client.resource;

import jpize.gl.shader.Shader;
import jpize.util.res.Resource;

public class ResourceShader extends ResourceHandle<Shader> {

    private final Shader shader;

    public ResourceShader(String ID, String path) {
        super(ID, path);
        this.shader = new Shader();
    }

    @Override
    public Shader object() {
        return shader;
    }

    @Override
    public void reload() {
        final String vertexName = (super.getPath() + ".vsh");
        final Resource vertexRes = Resource.external("assets/resources/" + vertexName);

        final String fragmentName = (super.getPath() + ".fsh");
        final Resource fragmentRes = Resource.external("assets/resources/" + fragmentName);

        shader.load(vertexRes, fragmentRes);
    }

    @Override
    public void dispose() {
        shader.dispose();
    }

}
