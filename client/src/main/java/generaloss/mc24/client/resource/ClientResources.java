package generaloss.mc24.client.resource;

import generaloss.mc24.server.block.BlockState;
import jpize.util.res.handle.IResourceSource;
import jpize.util.res.handle.ResourceHandleMap;
import jpize.util.time.Stopwatch;

public class ClientResources {

    public static final ResourceHandleMap<String, TextureAtlasHandle> ATLASES = new ResourceHandleMap<>();
    public static final ResourceHandleMap<String, MusicHandle> MUSICS = new ResourceHandleMap<>(MusicHandle::new);
    public static final ResourceHandleMap<String, ShaderHandle> SHADERS = new ResourceHandleMap<>(ShaderHandle::new);
    public static final ResourceHandleMap<String, SkyboxHandle> SKYBOXES = new ResourceHandleMap<>(SkyboxHandle::new);
    public static final ResourceHandleMap<String, Texture2DHandle> TEXTURES = new ResourceHandleMap<>(Texture2DHandle::new);
    public static final ResourceHandleMap<String, FontHandle> FONTS = new ResourceHandleMap<>(FontHandle::new);
    public static final ResourceHandleMap<BlockState, BlockModelHandle> BLOCK_STATE_MODELS = new ResourceHandleMap<>(BlockModelHandle::new);

    public static void init(IResourceSource resourceSource) {
        ATLASES.setSource(resourceSource);
        MUSICS.setSource(resourceSource);
        SHADERS.setSource(resourceSource);
        SKYBOXES.setSource(resourceSource);
        TEXTURES.setSource(resourceSource);
        FONTS.setSource(resourceSource);
        BLOCK_STATE_MODELS.setSource(resourceSource);
    }

    public static void reload() {
        final Stopwatch stopwatch = new Stopwatch().start();
        ATLASES.reload();
        MUSICS.reload();
        SHADERS.reload();
        SKYBOXES.reload();
        TEXTURES.reload();
        BLOCK_STATE_MODELS.reload();
        FONTS.reload();
        System.out.println("[INFO]: Reloaded client resources in " + stopwatch.getMillis() + " ms.");
    }

    public static void dispose() {
        ATLASES.dispose();
        MUSICS.dispose();
        SHADERS.dispose();
        SKYBOXES.dispose();
        TEXTURES.dispose();
        FONTS.dispose();
        BLOCK_STATE_MODELS.dispose();
    }

}
