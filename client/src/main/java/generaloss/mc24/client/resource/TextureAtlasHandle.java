package generaloss.mc24.client.resource;

import jpize.gl.texture.GlFilter;
import jpize.util.atlas.TextureAtlas;
import jpize.util.res.ZipResource;
import jpize.util.res.handle.IResourceSource;
import jpize.util.res.handle.ResourceHandle;

public class TextureAtlasHandle extends ResourceHandle<String, TextureAtlas<String>> {

    private final TextureAtlas<String> atlas;
    private final int width, height;
    private ZipResource[] resources;

    public TextureAtlasHandle(String key, String path, int width, int height) {
        super(key, path);
        this.atlas = new TextureAtlas<>();
        this.width = width;
        this.height = height;

        this.atlas.setPadding(4);
        this.atlas.setFillPaddings(true);
        this.atlas.getTexture()
            .setFilters(GlFilter.LINEAR, GlFilter.NEAREST)
            .generateMipmap(0, 1)
            .setMaxAnisotropy(1);
    }

    @Override
    public TextureAtlas<String> resource() {
        return atlas;
    }

    @Override
    public void load(IResourceSource source, String path) {
        if(resources == null)
            resources = ((ZipResource) source.getResource(path)).listResources();

        for(ZipResource resource: resources){
            if(resource.isDir())
                continue;
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
