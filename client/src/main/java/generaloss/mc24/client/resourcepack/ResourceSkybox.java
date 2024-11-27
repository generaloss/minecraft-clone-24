package generaloss.mc24.client.resourcepack;

import generaloss.mc24.server.resourcepack.ResourceHandle;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.gl.texture.GlCubemapTarget;
import jpize.gl.texture.Skybox;
import jpize.util.pixmap.Pixmap;
import jpize.util.pixmap.PixmapIO;
import jpize.util.res.Resource;

import java.util.Collection;

public class ResourceSkybox extends ResourceHandle<String, Skybox> {

    private static final String[] POSTFIX_ARRAY = { "1", "3", "4", "5", "0", "2" };

    private final String ID;
    private final Skybox skybox;

    public ResourceSkybox(String ID, String path) {
        super(path);
        this.ID = ID;
        this.skybox = new Skybox();
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public Skybox getObject() {
        return skybox;
    }

    @Override
    public void load(ResourcePack pack) {
        for(GlCubemapTarget target: GlCubemapTarget.values()) {
            final String path = super.getPath().replace("%s", POSTFIX_ARRAY[target.ordinal()]);
            final Resource resource = pack.get(path);

            final Pixmap pixmap = PixmapIO.load(resource);
            skybox.setImage(target, pixmap);
            pixmap.dispose();
        }
    }

    @Override
    public void reload(Collection<ResourcePack> packs) {
        for(GlCubemapTarget target: GlCubemapTarget.values()) {
            final String path = super.getPath().replace("%s", POSTFIX_ARRAY[target.ordinal()]);
            final Resource resource = super.getResourceFromPacks(packs, path);

            final Pixmap pixmap = PixmapIO.load(resource);
            skybox.setImage(target, pixmap);
            pixmap.dispose();
        }
    }

    @Override
    public void dispose() {
        skybox.dispose();
    }

}