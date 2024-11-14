package generaloss.mc24.client.resource;

import jpize.gl.texture.GlCubemapTarget;
import jpize.gl.texture.Skybox;
import jpize.util.pixmap.Pixmap;
import jpize.util.pixmap.PixmapIO;
import jpize.util.res.Resource;

public class ResourceSkybox extends ResourceHandle<Skybox> {

    private static final String[] POSTFIX_ARRAY = { "positive_x", "negative_x", "positive_y", "negative_y", "positive_z", "negative_z" };

    private final Skybox skybox;

    public ResourceSkybox(ResourceDispatcher dispatcher, String identifier, String path) {
        super(dispatcher, identifier, path);
        this.skybox = new Skybox();
    }

    @Override
    public Skybox resource() {
        return skybox;
    }

    @Override
    public void reload() {
        for(GlCubemapTarget target: GlCubemapTarget.values()) {
            final String name = (super.getPath().replace("%s", POSTFIX_ARRAY[target.ordinal()]));

            Resource resource = Resource.external(super.dispatcher().getRootDirectory() + name);
            if(!resource.exists())
                resource = Resource.external(ResourceDispatcher.DEFAULT_ROOT_DIR + name);

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