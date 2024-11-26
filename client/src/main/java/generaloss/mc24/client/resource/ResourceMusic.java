package generaloss.mc24.client.resource;

import jpize.audio.util.AlMusic;
import jpize.util.res.Resource;

public class ResourceMusic extends ResourceHandle<AlMusic> {

    private final AlMusic music;

    public ResourceMusic(String ID, String path) {
        super(ID, path);
        this.music = new AlMusic();
    }

    @Override
    public AlMusic object() {
        return music;
    }

    @Override
    public void reload() {
        final Resource resource = Resource.external("assets/resources/" + super.getPath());
        music.load(resource);
    }

    @Override
    public void dispose() {
        music.dispose();
    }

}