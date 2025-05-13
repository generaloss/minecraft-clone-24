package generaloss.mc24.client.resources.handle;

import jpize.opengl.texture.GLFilter;
import jpize.util.atlas.TextureAtlas;
import jpize.util.res.ZipResource;
import jpize.util.res.ResourceSource;
import jpize.util.res.handle.ResHandle;

import java.util.HashMap;
import java.util.Map;

public class TextureAtlasHandle extends ResHandle<String, TextureAtlas<String>> {

    private final TextureAtlas<String> atlas;
    private final int width, height;
    private final Map<String, ZipResource> loadedResources;

    public TextureAtlasHandle(String key, String path, int width, int height) {
        super(key, path);
        this.atlas = new TextureAtlas<>();
        this.width = width;
        this.height = height;

        this.atlas.setPadding(4);
        this.atlas.enablePaddingFilling(true);
        this.atlas.getTexture()
            .setFilters(GLFilter.LINEAR, GLFilter.NEAREST)
            .generateMipmap(0, 4)
            .setMaxAnisotropy(16)
            .setLodBias(-1);

        this.loadedResources = new HashMap<>();
    }

    @Override
    public TextureAtlas<String> resource() {
        return atlas;
    }

    @Override
    public void load(ResourceSource source, String path) {
        final ZipResource[] resources = source.getResource(path).asZipRes().listResources();
        for(ZipResource resource: resources) {
            if(resource.isDir())
                continue;
            loadedResources.put(resource.name(), resource);
        }

        for(ZipResource resource: loadedResources.values()){
            final String key = resource.path().substring(path.length());
            atlas.put(key, resource);
        }
        atlas.build(width, height);
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }

}
