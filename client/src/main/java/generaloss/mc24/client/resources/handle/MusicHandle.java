package generaloss.mc24.client.resources.handle;

import jpize.audio.util.AlMusic;
import jpize.util.res.Resource;
import jpize.util.res.ResourceSource;
import jpize.util.res.handle.ResHandle;

public class MusicHandle extends ResHandle<String, AlMusic> {

    private final AlMusic music;

    public MusicHandle(String key, String path) {
        super(key, path);
        this.music = new AlMusic();
    }

    @Override
    public AlMusic resource() {
        return music;
    }

    @Override
    public void load(ResourceSource source, String path) {
        final Resource resource = source.getResource(path);
        music.load(resource);
    }

    @Override
    public void dispose() {
        music.dispose();
    }

}