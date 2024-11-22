package generaloss.mc24.client.resource;

import generaloss.mc24.server.resource.ResourceDispatcher;
import generaloss.mc24.server.resource.ResourceHandle;
import jpize.gl.shader.Shader;
import jpize.util.res.Resource;

public class ResourceShader extends ResourceHandle<Shader> {

    private final Shader shader;

    public ResourceShader(ResourceDispatcher dispatcher, String identifier, String path) {
        super(dispatcher, identifier, path);
        this.shader = new Shader();
    }

    @Override
    public Shader resource() {
        return shader;
    }

    @Override
    public void reload() {
        final String vertexName = (super.getPath() + ".vsh");
        Resource vertexRes = Resource.external(super.dispatcher().getDirectory() + vertexName);
        if(!vertexRes.exists())
            vertexRes = Resource.external(super.dispatcher().getDefaultDirectory() + vertexName);

        final String fragmentName = (super.getPath() + ".fsh");
        Resource fragmentRes = Resource.external(super.dispatcher().getDirectory() + fragmentName);
        if(!fragmentRes.exists())
            fragmentRes = Resource.external(super.dispatcher().getDefaultDirectory() + fragmentName);

        shader.load(vertexRes, fragmentRes);
    }

    @Override
    public void dispose() {
        shader.dispose();
    }

}
