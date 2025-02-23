package generaloss.mc24.client.resources.handle;

import jpize.gl.texture.Texture2D;
import jpize.util.res.Resource;
import jpize.util.res.ResourceSource;
import jpize.util.res.handle.ResHandle;

public class Texture2DHandle extends ResHandle<String, Texture2D> {

    private final Texture2D texture;

    public Texture2DHandle(String key, String path) {
        super(key, path);
        this.texture = new Texture2D();
    }

    @Override
    public void load(ResourceSource source, String path) {
        final Resource resource = source.getResource(path);
        texture.setImage(resource);
    }

    @Override
    public Texture2D resource() {
        return texture;
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

}
