package generaloss.mc24.client.resource;

import generaloss.mc24.server.resource.ResourceDispatcher;
import generaloss.mc24.server.resource.ResourceHandle;
import jpize.gl.texture.GlCubemapTarget;
import jpize.gl.texture.Skybox;
import jpize.util.pixmap.Pixmap;
import jpize.util.pixmap.PixmapIO;
import jpize.util.res.Resource;

public class ResourceSkybox extends ResourceHandle<Skybox> {

    private static final String[] POSTFIX_ARRAY = { "1", "3", "4", "5", "0", "2" };

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

            Resource resource = Resource.external(super.dispatcher().getDirectory() + name);
            if(!resource.exists())
                resource = Resource.external(super.dispatcher().getDefaultDirectory() + name);

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