package generaloss.mc24.client.resourcepack;

import generaloss.mc24.server.resourcepack.ResourceHandle;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.gl.texture.GlFilter;
import jpize.gl.texture.Texture2D;
import jpize.util.array.StringList;
import jpize.util.atlas.TextureAtlas;
import jpize.util.region.TextureRegion;
import jpize.util.res.Resource;
import jpize.util.res.ZipEntryResource;

import java.util.*;

public class ResourceAtlas extends ResourceHandle<String, Texture2D> {

    private final String ID;
    private final int width, height;
    private final StringList toLoadPaths;
    private final TextureAtlas<String> atlas;

    public ResourceAtlas(String ID, String directoryPath, int width, int height) {
        super(directoryPath);
        this.ID = ID;
        this.width = width;
        this.height = height;
        this.toLoadPaths = new StringList();
        // atlas
        this.atlas = new TextureAtlas<>();
        this.atlas.setPadding(4);
        this.atlas.setFillPaddings(true);
        this.atlas.getTexture()
            .setFilters(GlFilter.LINEAR, GlFilter.NEAREST)
            .generateMipmap(0, 1)
            .setMaxAnisotropy(1);
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public Texture2D getObject() {
        return atlas.getTexture();
    }

    public TextureRegion getRegion(String identifier) {
        return atlas.getRegion(identifier);
    }

    @Override
    public void load(ResourcePack defaultPack) {
        final ZipEntryResource directoryRes = defaultPack.get(super.getPath());
        final ZipEntryResource[] list = directoryRes.listRes();
        for(ZipEntryResource resource : list){
            if(resource.isDir())
                continue;
            toLoadPaths.add(resource.path());
        }
        this.reload(List.of(defaultPack));
    }

    @Override
    public void reload(Collection<ResourcePack> packs) {
        for(String path: toLoadPaths){
            final Resource resource = super.getResourceFromPacks(packs, path);
            atlas.put(path.substring(super.getPath().length()), resource);
        }
        atlas.build(width, height);
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }

}
