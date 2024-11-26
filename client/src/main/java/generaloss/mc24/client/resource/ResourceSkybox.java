package generaloss.mc24.client.resource;

import jpize.gl.texture.GlCubemapTarget;
import jpize.gl.texture.Skybox;
import jpize.util.pixmap.Pixmap;
import jpize.util.pixmap.PixmapIO;
import jpize.util.res.Resource;

public class ResourceSkybox extends ResourceHandle<Skybox> {

    private static final String[] POSTFIX_ARRAY = { "1", "3", "4", "5", "0", "2" };

    private final Skybox skybox;

    public ResourceSkybox(String ID, String path) {
        super(ID, path);
        this.skybox = new Skybox();
    }

    @Override
    public Skybox object() {
        return skybox;
    }

    @Override
    public void reload() {
        for(GlCubemapTarget target: GlCubemapTarget.values()) {
            final String name = (super.getPath().replace("%s", POSTFIX_ARRAY[target.ordinal()]));
            final Resource resource = Resource.external("assets/resources/" + name);

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