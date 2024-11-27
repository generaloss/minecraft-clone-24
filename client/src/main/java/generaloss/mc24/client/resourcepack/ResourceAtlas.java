package generaloss.mc24.client.resourcepack;

import generaloss.mc24.server.resourcepack.ResourceHandle;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.gl.texture.GlFilter;
import jpize.gl.texture.Texture2D;
import jpize.util.atlas.TextureAtlas;
import jpize.util.region.TextureRegion;
import jpize.util.res.ExternalResource;
import jpize.util.res.Resource;

import java.util.HashMap;
import java.util.Map;

public class ResourceAtlas extends ResourceHandle<String, Texture2D> {

    private final int width, height;
    private final Map<String, String> paths;
    private final TextureAtlas<String> atlas;

    public ResourceAtlas(ResourcePack defaultPack, String ID, String path, int width, int height) {
        super(defaultPack, ID, path);
        this.width = width;
        this.height = height;
        this.paths = new HashMap<>();

        this.atlas = new TextureAtlas<>();
        this.atlas.setPadding(4);
        this.atlas.setFillPaddings(true);
        this.atlas.getTexture()
            .setFilters(GlFilter.LINEAR, GlFilter.NEAREST)
            .generateMipmap(0, 1)
            .setMaxAnisotropy(1);
    }

    @Override
    public Texture2D object() {
        return atlas.getTexture();
    }

    public ResourceAtlas register(String identifier, String name) {
        this.paths.put(identifier, name);
        return this;
    }

    public ResourceAtlas registerAllInDirectory() {
        final ExternalResource directoryRes = Resource.external("assets/resources/" + super.getPath());
        for(ExternalResource resource: directoryRes.listRes())
            this.register(resource.simpleName(), "/" + resource.name());
        return this;
    }

    public TextureRegion getRegion(String identifier) {
        return atlas.getRegion(identifier);
    }

    @Override
    public void load(ResourcePack pack) {
        for(Map.Entry<String, String> entry: paths.entrySet()){
            final String name = (super.getPath() + entry.getValue());
            final Resource resource = pack.getOrDefault(name, super.getDefaultPack());

            atlas.put(entry.getKey(), resource);
        }

        atlas.build(width, height);
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }

}
