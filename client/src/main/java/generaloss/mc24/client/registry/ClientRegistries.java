package generaloss.mc24.client.registry;

import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.util.Disposable;
import jpize.util.time.Stopwatch;

import java.util.Collection;

public class ClientRegistries extends Registries implements Disposable {

    public final RegistryAtlases ATLASES;
    public final RegistryMusics MUSICS;
    public final RegistryShaders SHADERS;
    public final RegistrySkyboxes SKYBOXES;
    public final RegistryTextures TEXTURES;
    public final RegistryFonts FONTS;
    public final RegistryBlockModels BLOCK_MODELS;

    public ClientRegistries(ResourcePack defaultPack) {
        super(defaultPack);
        this.ATLASES = new RegistryAtlases();
        this.MUSICS = new RegistryMusics();
        this.SHADERS = new RegistryShaders();
        this.SKYBOXES = new RegistrySkyboxes();
        this.TEXTURES = new RegistryTextures();
        this.FONTS = new RegistryFonts();
        this.BLOCK_MODELS = new RegistryBlockModels(BLOCKS);
    }

    @Override
    public void loadResources(ResourcePack defaultPack) {
        super.loadResources(defaultPack);
        final Stopwatch stopwatch = new Stopwatch().start();
        ATLASES.load(defaultPack);
        MUSICS.load(defaultPack);
        SHADERS.load(defaultPack);
        SKYBOXES.load(defaultPack);
        TEXTURES.load(defaultPack);
        FONTS.load(defaultPack);
        BLOCK_MODELS.load(defaultPack);
        System.out.println("[INFO]: Loaded client resources in " + stopwatch.getMillis() + " ms.");
    }

    @Override
    public void reloadResources(Collection<ResourcePack> packs) {
        super.reloadResources(packs);
        final Stopwatch stopwatch = new Stopwatch().start();
        ATLASES.reload(packs);
        MUSICS.reload(packs);
        SHADERS.reload(packs);
        SKYBOXES.reload(packs);
        TEXTURES.reload(packs);
        BLOCK_MODELS.reload(packs);
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
        BLOCK_MODELS.dispose();
    }

}
