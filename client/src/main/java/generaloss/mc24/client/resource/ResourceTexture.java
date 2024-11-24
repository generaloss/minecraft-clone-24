package generaloss.mc24.client.resource;

import jpize.gl.texture.Texture2D;
import jpize.util.pixmap.Pixmap;
import jpize.util.pixmap.PixmapIO;
import jpize.util.res.Resource;

public class ResourceTexture extends ResourceHandle<Texture2D> {

    private final Texture2D texture;

    public ResourceTexture(ResourcesRegistry dispatcher, String identifier, String path) {
        super(dispatcher, identifier, path);
        this.texture = new Texture2D();
    }

    @Override
    public Texture2D resource() {
        return texture;
    }

    @Override
    public void reload() {
        Resource resource = Resource.external(super.dispatcher().getDirectory() + super.getPath());
        if(!resource.exists())
            resource = Resource.external(super.dispatcher().getDefaultDirectory() + super.getPath());

        final Pixmap pixmap = PixmapIO.load(resource);
        texture.setImage(pixmap);
        pixmap.dispose();
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

}
