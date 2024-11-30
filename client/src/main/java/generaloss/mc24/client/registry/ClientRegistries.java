package generaloss.mc24.client.registry;

import generaloss.mc24.client.block.BlockStateModel;
import generaloss.mc24.client.resourcepack.*;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.registry.ResourceRegistry;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.audio.util.AlMusic;
import jpize.gl.shader.Shader;
import jpize.gl.texture.Skybox;
import jpize.gl.texture.Texture2D;
import jpize.util.Disposable;
import jpize.util.atlas.TextureAtlas;
import jpize.util.font.Font;
import jpize.util.time.Stopwatch;

import java.util.Collection;

public class ClientRegistries extends Registries implements Disposable {

    private final ResourceRegistry<String, ResourceAtlas> atlas;
    private final ResourceRegistry<String, ResourceMusic> music;
    private final ResourceRegistry<String, ResourceShader> shader;
    private final ResourceRegistry<String, ResourceSkybox> skybox;
    private final ResourceRegistry<String, ResourceTexture> texture;
    private final ResourceRegistry<String, ResourceFont> font;
    private final ResourceRegistry<BlockState, ResourceBlockStateModel> blockModel;

    public ClientRegistries(ResourcePack defaultPack) {
        super(defaultPack);
        this.atlas = new ResourceRegistry<>();
        this.music = new ResourceRegistry<>();
        this.shader = new ResourceRegistry<>();
        this.skybox = new ResourceRegistry<>();
        this.texture = new ResourceRegistry<>();
        this.font = new ResourceRegistry<>();
        this.blockModel = new ResourceRegistry<>();
    }


    public TextureAtlas<String> getAtlas(String ID) {
        return atlas.get(ID).getObject();
    }

    public ResourceAtlas registerAtlas(String ID, String path, int width, int height) {
        return atlas.register(new ResourceAtlas(ID, path, width, height));
    }


    public AlMusic getMusic(String ID) {
        return music.get(ID).getObject();
    }

    public ResourceMusic registerMusic(String ID, String path) {
        return music.register(new ResourceMusic(ID, path));
    }


    public Shader getShader(String ID) {
        return shader.get(ID).getObject();
    }

    public ResourceShader registerShader(String ID, String path) {
        return shader.register(new ResourceShader(ID, path));
    }


    public Skybox getSkybox(String ID) {
        return skybox.get(ID).getObject();
    }

    public ResourceSkybox registerSkybox(String ID, String path) {
        return skybox.register(new ResourceSkybox(ID, path));
    }


    public Texture2D getTexture(String ID) {
        return texture.get(ID).getObject();
    }

    public ResourceTexture registerTexture(String ID, String path) {
        return texture.register(new ResourceTexture(ID, path));
    }


    public Font getFont(String ID) {
        return font.get(ID).getObject();
    }

    public ResourceFont registerFont(String ID, String path) {
        return font.register(new ResourceFont(ID, path));
    }


    public BlockStateModel getBlockModel(BlockState ID) {
        return blockModel.get(ID).getObject();
    }

    public ResourceBlockStateModel registerBlockModel(String path) {
        return blockModel.register(new ResourceBlockStateModel(path, this));
    }

    public ResourceBlockStateModel registerBlockModel(ResourceBlockStateModel blockStateModelResource) {
        return blockModel.register(blockStateModelResource);
    }


    @Override
    public void loadResources(ResourcePack defaultPack) {
        super.loadResources(defaultPack);
        final Stopwatch stopwatch = new Stopwatch().start();
        atlas.load(defaultPack);
        music.load(defaultPack);
        shader.load(defaultPack);
        skybox.load(defaultPack);
        texture.load(defaultPack);
        font.load(defaultPack);
        blockModel.load(defaultPack);
        System.out.println("[Client] Loaded resources in " + stopwatch.getMillis() + " ms.");
    }

    public void reloadResources(Collection<ResourcePack> packs) {
        final Stopwatch stopwatch = new Stopwatch().start();
        atlas.reload(packs);
        music.reload(packs);
        shader.reload(packs);
        skybox.reload(packs);
        texture.reload(packs);
        blockModel.reload(packs);
        font.reload(packs);
        System.out.println("[Client] Reloaded resources in " + stopwatch.getMillis() + " ms.");
    }

    @Override
    public void dispose() {
        atlas.dispose();
        music.dispose();
        shader.dispose();
        skybox.dispose();
        texture.dispose();
        font.dispose();
        blockModel.dispose();
    }

}
