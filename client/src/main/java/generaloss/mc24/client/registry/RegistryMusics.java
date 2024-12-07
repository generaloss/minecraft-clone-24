package generaloss.mc24.client.registry;

import generaloss.mc24.client.resourcepack.ResourceMusic;
import generaloss.mc24.server.registry.ResourceRegistry;
import jpize.audio.util.AlMusic;

public class RegistryMusics extends ResourceRegistry<String, ResourceMusic> {

    public AlMusic get(String ID) {
        return super.getResource(ID).getObject();
    }

    public ResourceMusic register(String ID, String path) {
        return super.registerResource(new ResourceMusic(ID, path));
    }

}
