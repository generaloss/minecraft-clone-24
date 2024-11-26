package generaloss.mc24.client.resource;

import jpize.audio.util.AlMusic;
import jpize.util.res.Resource;

public class ResourceMusic extends ResourceHandle<AlMusic> {

    private final AlMusic music;

    public ResourceMusic(ResourcePack defaultPack, String ID, String path) {
        super(defaultPack, ID, path);
        this.music = new AlMusic();
    }

    @Override
    public AlMusic object() {
        return music;
    }

    @Override
    public void load(ResourcePack pack) {
        final Resource resource = pack.getOrDefault(super.getPath(), super.getDefaultPack());
        music.load(resource);
    }

    @Override
    public void dispose() {
        music.dispose();
    }

}