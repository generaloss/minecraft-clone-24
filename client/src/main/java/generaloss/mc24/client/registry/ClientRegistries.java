package generaloss.mc24.client.registry;

import generaloss.mc24.server.resourcepack.ResourcePack;
import generaloss.mc24.server.resourcepack.ResourcePackManager;
import jpize.util.Disposable;
import jpize.util.time.Stopwatch;

import java.util.Collection;

public class ClientRegistries implements Disposable {

    private final ResourcePackManager packManager;

    public final RegistryAtlases ATLASES;
    public final RegistryMusics MUSICS;
    public final RegistryShaders SHADERS;
    public final RegistrySkyboxes SKYBOXES;
    public final RegistryTextures TEXTURES;
    public final RegistryFonts FONTS;
    public final RegistryBlockModels BLOCK_STATE_MODELS;

    public ClientRegistries(ResourcePackManager packManager) {
        this.packManager = packManager;

        this.ATLASES = new RegistryAtlases();
        this.MUSICS = new RegistryMusics();
        this.SHADERS = new RegistryShaders();
        this.SKYBOXES = new RegistrySkyboxes();
        this.TEXTURES = new RegistryTextures();
        this.FONTS = new RegistryFonts();
        this.BLOCK_STATE_MODELS = new RegistryBlockModels();
    }

    public void loadResources() {
        final Stopwatch stopwatch = new Stopwatch().start();

        final ResourcePack corePack = packManager.getCorePack();
        ATLASES.load(corePack);
        MUSICS.load(corePack);
        SHADERS.load(corePack);
        SKYBOXES.load(corePack);
        TEXTURES.load(corePack);
        FONTS.load(corePack);
        BLOCK_STATE_MODELS.load(corePack);

        System.out.println("[INFO]: Loaded client resources in " + stopwatch.getMillis() + " ms.");
    }

    public void reloadResources() {
        final Stopwatch stopwatch = new Stopwatch().start();

        final Collection<ResourcePack> packs = packManager.getActivePacks();
        ATLASES.reload(packs);
        MUSICS.reload(packs);
        SHADERS.reload(packs);
        SKYBOXES.reload(packs);
        TEXTURES.reload(packs);
        BLOCK_STATE_MODELS.reload(packs);
        FONTS.reload(packs);

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
