package generaloss.mc24.client.resource;

import jpize.gl.texture.GlFilter;
import jpize.gl.texture.Texture2D;
import jpize.util.atlas.TextureAtlas;
import jpize.util.region.TextureRegion;
import jpize.util.res.ExternalResource;
import jpize.util.res.Resource;

import java.util.HashMap;
import java.util.Map;

public class ResourceAtlas extends ResourceHandle<Texture2D> {

    private final int width, height;
    private final Map<String, String> paths;
    private final TextureAtlas<String> atlas;

    public ResourceAtlas(ResourcesRegistry dispatcher, String identifier, String path, int width, int height) {
        super(dispatcher, identifier, path);
        this.width = width;
        this.height = height;
        this.paths = new HashMap<>();

        this.atlas = new TextureAtlas<>();
        this.atlas.setPadding(4);
        this.atlas.setFillPaddings(true);
        this.atlas.getTexture()
            .setFilters(GlFilter.LINEAR, GlFilter.NEAREST)
            .generateMipmap(0, 1)
            .setMaxAnisotropy(4);
    }

    @Override
    public Texture2D resource() {
        return atlas.getTexture();
    }

    public ResourceAtlas register(String identifier, String name) {
        this.paths.put(identifier, name);
        return this;
    }

    public ResourceAtlas registerAllInDirectory() {
        final ExternalResource directoryRes = Resource.external(super.dispatcher().getDirectory() + super.getPath());
        for(ExternalResource resource: directoryRes.listRes())
            register(resource.simpleName(), "/" + resource.name());
        return this;
    }

    public TextureRegion getRegion(String identifier) {
        return atlas.getRegion(identifier);
    }

    @Override
    public void reload() {
        for(Map.Entry<String, String> entry: paths.entrySet()){
            final String name = (super.getPath() + entry.getValue());

            Resource resource = Resource.external(super.dispatcher().getDirectory() + name);
            if(!resource.exists())
                resource = Resource.external(super.dispatcher().getDefaultDirectory() + name);

            atlas.put(entry.getKey(), resource);
        }

        atlas.build(width, height);
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }

}
