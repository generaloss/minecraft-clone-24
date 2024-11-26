package generaloss.mc24.client.resource;

import jpize.gl.texture.Texture2D;
import jpize.util.pixmap.Pixmap;
import jpize.util.pixmap.PixmapIO;
import jpize.util.res.Resource;

public class ResourceTexture extends ResourceHandle<Texture2D> {

    private final Texture2D texture;

    public ResourceTexture(ResourcePack defaultPack, String ID, String path) {
        super(defaultPack, ID, path);
        this.texture = new Texture2D();
    }

    @Override
    public Texture2D object() {
        return texture;
    }

    @Override
    public void load(ResourcePack pack) {
        final Resource resource = pack.getOrDefault(super.getPath(), super.getDefaultPack());
        final Pixmap pixmap = PixmapIO.load(resource);
        texture.setImage(pixmap);
        pixmap.dispose();
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

}
