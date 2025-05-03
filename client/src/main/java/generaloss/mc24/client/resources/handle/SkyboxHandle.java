package generaloss.mc24.client.resources.handle;

import jpize.opengl.texture.GLCubemapTarget;
import jpize.opengl.texture.Skybox;
import jpize.util.pixmap.Pixmap;
import jpize.util.pixmap.PixmapIO;
import jpize.util.res.Resource;
import jpize.util.res.ResourceSource;
import jpize.util.res.handle.ResHandle;

public class SkyboxHandle extends ResHandle<String, Skybox> {

    private static final String[] POSTFIX_ARRAY = { "1", "3", "4", "5", "0", "2" };

    private final Skybox skybox;

    public SkyboxHandle(String key, String path) {
        super(key, path);
        this.skybox = new Skybox();
    }

    @Override
    public Skybox resource() {
        return skybox;
    }

    @Override
    public void load(ResourceSource source, String path) {
        for(GLCubemapTarget target: GLCubemapTarget.values()) {
            final String sidePath = path.replace("%s", POSTFIX_ARRAY[target.ordinal()]);
            final Resource resource = source.getResource(sidePath);

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