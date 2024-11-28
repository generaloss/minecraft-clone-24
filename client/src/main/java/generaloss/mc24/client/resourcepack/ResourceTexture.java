package generaloss.mc24.client.resourcepack;

import generaloss.mc24.server.resourcepack.ResourceHandle;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.gl.texture.Texture2D;
import jpize.util.pixmap.Pixmap;
import jpize.util.pixmap.PixmapIO;
import jpize.util.res.Resource;

import java.util.Collection;

public class ResourceTexture extends ResourceHandle<String, Texture2D> {

    private final String ID;
    private final Texture2D texture;

    public ResourceTexture(String ID, String path) {
        super(path);
        this.ID = ID;
        this.texture = new Texture2D();
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public Texture2D getObject() {
        return texture;
    }

    @Override
    public void load(ResourcePack pack) {
        final Resource resource = pack.getResource(super.getPath());
        final Pixmap pixmap = PixmapIO.load(resource);
        texture.setImage(pixmap);
        pixmap.dispose();
    }

    @Override
    public void reload(Collection<ResourcePack> packs) {
        final Resource resource = super.getResourceFromPacks(packs, super.getPath());
        final Pixmap pixmap = PixmapIO.load(resource);
        texture.setImage(pixmap);
        pixmap.dispose();
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

}
