package generaloss.mc24.client.resourcepack;

import jpize.audio.util.AlMusic;
import jpize.util.res.Resource;
import jpize.util.res.handle.IResourceSource;
import jpize.util.res.handle.ResourceHandle;

public class MusicHandle extends ResourceHandle<String, AlMusic> {

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
    public void load(IResourceSource source, String path) {
        final Resource resource = source.getResource(path);
        music.load(resource);
    }

    @Override
    public void dispose() {
        music.dispose();
    }

}