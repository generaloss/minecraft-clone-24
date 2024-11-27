package generaloss.mc24.client.resourcepack;

import generaloss.mc24.server.resourcepack.ResourceHandle;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.gl.texture.GlCubemapTarget;
import jpize.gl.texture.Skybox;
import jpize.util.pixmap.Pixmap;
import jpize.util.pixmap.PixmapIO;
import jpize.util.res.Resource;

public class ResourceSkybox extends ResourceHandle<String, Skybox> {

    private static final String[] POSTFIX_ARRAY = { "1", "3", "4", "5", "0", "2" };

    private final Skybox skybox;

    public ResourceSkybox(ResourcePack defaultPack, String ID, String path) {
        super(defaultPack, ID, path);
        this.skybox = new Skybox();
    }

    @Override
    public Skybox object() {
        return skybox;
    }

    @Override
    public void load(ResourcePack pack) {
        for(GlCubemapTarget target: GlCubemapTarget.values()) {
            final String name = (super.getPath().replace("%s", POSTFIX_ARRAY[target.ordinal()]));
            final Resource resource = pack.getOrDefault(name, super.getDefaultPack());

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