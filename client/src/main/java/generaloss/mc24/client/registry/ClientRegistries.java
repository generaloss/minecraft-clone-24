package generaloss.mc24.client.registry;

import generaloss.mc24.client.block.BlockModel;
import generaloss.mc24.client.resource.*;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.registry.Registry;
import jpize.util.Disposable;

public class ClientRegistries extends Registries implements Disposable {

    private final Registry<BlockState, BlockModel> blockModel;

    private final Registry<String, ResourceAtlas> atlas;
    private final Registry<String, ResourceMusic> music;
    private final Registry<String, ResourceShader> shader;
    private final Registry<String, ResourceSkybox> skybox;
    private final Registry<String, ResourceTexture> texture;

    public ClientRegistries() {
        this.blockModel = new Registry<>();

        this.atlas = new Registry<>();
        this.music = new Registry<>();
        this.shader = new Registry<>();
        this.skybox = new Registry<>();
        this.texture = new Registry<>();
    }

    public Registry<BlockState, BlockModel> blockModel() {
        return blockModel;
    }

    public Registry<String, ResourceAtlas> atlas() {
        return atlas;
    }

    public Registry<String, ResourceMusic> music() {
        return music;
    }

    public Registry<String, ResourceShader> shader() {
        return shader;
    }

    public Registry<String, ResourceSkybox> skybox() {
        return skybox;
    }

    public Registry<String, ResourceTexture> texture() {
        return texture;
    }

    public void reloadResources() {
        for(ResourceAtlas res : atlas) res.reload();
        for(ResourceMusic res : music) res.reload();
        for(ResourceShader res : shader) res.reload();
        for(ResourceSkybox res : skybox) res.reload();
        for(ResourceTexture res : texture) res.reload();
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
