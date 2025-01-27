package generaloss.mc24.client.resourcepack;

import jpize.util.font.Font;
import jpize.util.res.Resource;
import jpize.util.res.handle.IResourceSource;
import jpize.util.res.handle.ResourceHandle;

public class FontHandle extends ResourceHandle<String, Font> {

    private final Font font;

    public FontHandle(String key, String path) {
        super(key, path);
        this.font = new Font();
    }

    @Override
    public Font resource() {
        return font;
    }

    @Override
    public void load(IResourceSource source, String path) {
        final Resource resource = source.getResource(path);
        font.loadFNT(resource, false);
    }


    @Override
    public void dispose() {
        font.dispose();
    }

}