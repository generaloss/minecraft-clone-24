package generaloss.mc24.client.resourcepack;

import generaloss.mc24.server.resourcepack.ResourceHandle;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.gl.shader.Shader;
import jpize.util.res.Resource;

import java.util.Collection;

public class ResourceShader extends ResourceHandle<String, Shader> {

    private final String ID;
    private final Shader shader;

    public ResourceShader(String ID, String path) {
        super(path);
        this.ID = ID;
        this.shader = new Shader();
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public Shader getObject() {
        return shader;
    }

    @Override
    public void load(ResourcePack pack) {
        final Resource vertexRes = pack.getResource(super.getPath() + ".vsh");
        final Resource fragmentRes = pack.getResource(super.getPath() + ".fsh");
        shader.load(vertexRes, fragmentRes);
    }

    @Override
    public void reload(Collection<ResourcePack> packs) {
        final Resource vertexRes = super.getResourceFromPacks(packs, super.getPath() + ".vsh");
        final Resource fragmentRes = super.getResourceFromPacks(packs, super.getPath() + ".fsh");
        shader.load(vertexRes, fragmentRes);
    }

    @Override
    public void dispose() {
        shader.dispose();
    }

}
