package generaloss.mc24.client.resource;

import generaloss.mc24.server.resource.ResourceDispatcher;

public class ResourceDispatcherClient extends ResourceDispatcher {

    @Override
    public String getDefaultDirectory() {
        return "assets/client";
    }


    public ResourceTexture registerTexture(String identifier, String path) {
        return (ResourceTexture) this.register(new ResourceTexture(this, identifier, path));
    }

    public ResourceSkybox registerSkybox(String identifier, String path) {
        return (ResourceSkybox) this.register(new ResourceSkybox(this, identifier, path));
    }

    public ResourceMusic registerMusic(String identifier, String path) {
        return (ResourceMusic) this.register(new ResourceMusic(this, identifier, path));
    }

    public ResourceShader registerShader(String identifier, String path) {
        return (ResourceShader) this.register(new ResourceShader(this, identifier, path));
    }

    public ResourceAtlas registerAtlas(String identifier, String path, int width, int height) {
        return (ResourceAtlas) this.register(new ResourceAtlas(this, identifier, path, width, height));
    }

}
