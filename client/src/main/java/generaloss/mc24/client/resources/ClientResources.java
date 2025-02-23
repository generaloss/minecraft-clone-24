package generaloss.mc24.client.resources;

import generaloss.mc24.client.resources.handle.*;
import generaloss.mc24.server.block.BlockState;
import jpize.util.res.ResourceSource;
import jpize.util.res.handle.ResHandleMap;
import jpize.util.time.Stopwatch;

public class ClientResources {

    public static final ResHandleMap<String, TextureAtlasHandle> ATLASES = new ResHandleMap<>();
    public static final ResHandleMap<String, MusicHandle> MUSICS = new ResHandleMap<>(MusicHandle::new);
    public static final ResHandleMap<String, ShaderHandle> SHADERS = new ResHandleMap<>(ShaderHandle::new);
    public static final ResHandleMap<String, SkyboxHandle> SKYBOXES = new ResHandleMap<>(SkyboxHandle::new);
    public static final ResHandleMap<String, Texture2DHandle> TEXTURES = new ResHandleMap<>(Texture2DHandle::new);
    public static final ResHandleMap<String, FontHandle> FONTS = new ResHandleMap<>(FontHandle::new);
    public static final ResHandleMap<BlockState, BlockModelHandle> BLOCK_STATE_MODELS = new ResHandleMap<>(BlockModelHandle::new);

    public static void init(ResourceSource resourceSource) {
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
