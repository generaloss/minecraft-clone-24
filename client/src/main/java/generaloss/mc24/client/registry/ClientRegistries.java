package generaloss.mc24.client.registry;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.block.BlockStateModel;
import generaloss.mc24.client.resourcepack.*;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.registry.Registry;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.util.Disposable;
import jpize.util.time.Stopwatch;

public class ClientRegistries extends Registries implements Disposable {

    private final Main context;

    private final Registry<String, ResourceAtlas> atlas;
    private final Registry<String, ResourceMusic> music;
    private final Registry<String, ResourceShader> shader;
    private final Registry<String, ResourceSkybox> skybox;
    private final Registry<String, ResourceTexture> texture;

    private final Registry<BlockState, ResourceBlockStateModel> blockModel;

    public ClientRegistries(Main context) {
        this.context = context;

        this.atlas = new Registry<>();
        this.music = new Registry<>();
        this.shader = new Registry<>();
        this.skybox = new Registry<>();
        this.texture = new Registry<>();

        this.blockModel = new Registry<>();
    }


    public ResourceAtlas getAtlas(String ID) {
        return atlas.get(ID);
    }

    public ResourceMusic getMusic(String ID) {
        return music.get(ID);
    }

    public ResourceShader getShader(String ID) {
        return shader.get(ID);
    }

    public ResourceSkybox getSkybox(String ID) {
        return skybox.get(ID);
    }

    public ResourceTexture getTexture(String ID) {
        return texture.get(ID);
    }

    public BlockStateModel getBlockModel(BlockState ID) {
        return blockModel.get(ID).object();
    }


    public ResourceAtlas registerAtlas(String ID, String path, int width, int height) {
        return atlas.register(new ResourceAtlas(context.getDefaultPack(), ID, path, width, height));
    }

    public ResourceMusic registerMusic(String ID, String path) {
        return music.register(new ResourceMusic(context.getDefaultPack(), ID, path));
    }

    public ResourceShader registerShader(String ID, String path) {
        return shader.register(new ResourceShader(context.getDefaultPack(), ID, path));
    }

    public ResourceSkybox registerSkybox(String ID, String path) {
        return skybox.register(new ResourceSkybox(context.getDefaultPack(), ID, path));
    }

    public ResourceTexture registerTexture(String ID, String path) {
        return texture.register(new ResourceTexture(context.getDefaultPack(), ID, path));
    }

    public ResourceBlockStateModel registerBlockModel(String path) {
        return blockModel.register(new ResourceBlockStateModel(context.getDefaultPack(), this, path));
    }

    public ResourceBlockStateModel registerBlockModel(ResourceBlockStateModel blockStateModelResource) {
        return blockModel.register(blockStateModelResource);
    }


    public void loadResources(ResourcePack pack) {
        final Stopwatch stopwatch = new Stopwatch().start();
        for(ResourceAtlas res : atlas) res.load(pack);
        for(ResourceMusic res : music) res.load(pack);
        for(ResourceShader res : shader) res.load(pack);
        for(ResourceSkybox res : skybox) res.load(pack);
        for(ResourceTexture res : texture) res.load(pack);
        for(ResourceBlockStateModel res : blockModel) res.load(pack);
        System.out.println("Loaded resource pack '" + pack.getID() + "' in " + stopwatch.getMillis() + " ms.");
    }

    public void loadResources() {
        this.loadResources(context.getDefaultPack());
    }

    @Override
    public void dispose() {
        for(Disposable res : atlas) res.dispose();
        for(Disposable res : music) res.dispose();
        for(Disposable res : shader) res.dispose();
        for(Disposable res : skybox) res.dispose();
        for(Disposable res : texture) res.dispose();
    }

}
