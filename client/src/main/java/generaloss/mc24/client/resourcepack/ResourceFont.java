package generaloss.mc24.client.resourcepack;

import generaloss.mc24.server.resourcepack.ResourceHandle;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.util.font.Font;
import jpize.util.res.Resource;

import java.util.Collection;

public class ResourceFont extends ResourceHandle<String, Font> {

    private final String ID;
    private final Font font;

    public ResourceFont(String ID, String path) {
        super(path);
        this.ID = ID;
        this.font = new Font();
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public Font getObject() {
        return font;
    }

    @Override
    public void load(ResourcePack pack) {
        final Resource resource = pack.getResource(super.getPath());
        font.loadFNT(resource, false);
    }

    @Override
    public void reload(Collection<ResourcePack> packs) {
        final Resource resource = super.getResourceFromPacks(packs, super.getPath());
        font.loadFNT(resource, false);
    }

    @Override
    public void dispose() {
        font.dispose();
    }

}