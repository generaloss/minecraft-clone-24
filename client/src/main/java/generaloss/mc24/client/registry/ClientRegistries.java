package generaloss.mc24.client.registry;

import generaloss.mc24.client.resourcepack.*;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.resourcepack.ResourcePackManager;
import jpize.util.Disposable;
import jpize.util.res.handle.ResourceHandleMap;
import jpize.util.time.Stopwatch;

public class ClientRegistries implements Disposable {

    public final ResourceHandleMap<String, TextureAtlasHandle> ATLASES;
    public final ResourceHandleMap<String, MusicHandle> MUSICS;
    public final ResourceHandleMap<String, ShaderHandle> SHADERS;
    public final ResourceHandleMap<String, SkyboxHandle> SKYBOXES;
    public final ResourceHandleMap<String, Texture2DHandle> TEXTURES;
    public final ResourceHandleMap<String, FontHandle> FONTS;
    public final ResourceHandleMap<BlockState, BlockModelHandle> BLOCK_STATE_MODELS;

    public ClientRegistries(ResourcePackManager packManager) {
        this.ATLASES = new ResourceHandleMap<>(packManager);
        this.MUSICS = new ResourceHandleMap<>(packManager, MusicHandle::new);
        this.SHADERS = new ResourceHandleMap<>(packManager, ShaderHandle::new);
        this.SKYBOXES = new ResourceHandleMap<>(packManager, SkyboxHandle::new);
        this.TEXTURES = new ResourceHandleMap<>(packManager, Texture2DHandle::new);
        this.FONTS = new ResourceHandleMap<>(packManager, FontHandle::new);
        this.BLOCK_STATE_MODELS = new ResourceHandleMap<>(packManager, BlockModelHandle::new);
    }

    public void reloadResources() {
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

    @Override
    public void dispose() {
        ATLASES.dispose();
        MUSICS.dispose();
        SHADERS.dispose();
        SKYBOXES.dispose();
        TEXTURES.dispose();
        FONTS.dispose();
        BLOCK_STATE_MODELS.dispose();
    }

}
