package generaloss.mc24.client.resource;

import generaloss.mc24.server.resource.ResourceDispatcher;
import generaloss.mc24.server.resource.ResourceHandle;
import jpize.audio.util.AlMusic;
import jpize.util.res.Resource;

public class ResourceMusic extends ResourceHandle<AlMusic> {

    private final AlMusic music;

    public ResourceMusic(ResourceDispatcher dispatcher, String identifier, String path) {
        super(dispatcher, identifier, path);
        this.music = new AlMusic();
    }

    @Override
    public AlMusic resource() {
        return music;
    }

    @Override
    public void reload() {
        Resource resource = Resource.external(super.dispatcher().getDirectory() + super.getPath());
        if(!resource.exists())
            resource = Resource.external(super.dispatcher().getDefaultDirectory() + super.getPath());

        music.load(resource);
    }

    @Override
    public void dispose() {
        music.dispose();
    }

}