package generaloss.mc24.client.resource;

import jpize.gl.shader.Shader;
import jpize.util.res.Resource;

public class ResourceShader extends ResourceHandle<Shader> {

    private Shader shader;

    public ResourceShader(ResourceDispatcher dispatcher, String identifier, String path) {
        super(dispatcher, identifier, path);
    }

    @Override
    public Shader resource() {
        return shader;
    }

    @Override
    public void reload() {
        final String vertexName = (super.getPath() + ".vsh");
        Resource vertexRes = Resource.external(super.dispatcher().getRootDirectory() + vertexName);
        if(!vertexRes.exists())
            vertexRes = Resource.external(ResourceDispatcher.DEFAULT_ROOT_DIR + vertexName);

        final String fragmentName = (super.getPath() + ".fsh");
        Resource fragmentRes = Resource.external(super.dispatcher().getRootDirectory() + fragmentName);
        if(!fragmentRes.exists())
            fragmentRes = Resource.external(ResourceDispatcher.DEFAULT_ROOT_DIR + fragmentName);

        if(shader != null)
            shader.dispose();
        shader = new Shader(vertexRes, fragmentRes);
    }

    @Override
    public void dispose() {
        shader.dispose();
    }

}
