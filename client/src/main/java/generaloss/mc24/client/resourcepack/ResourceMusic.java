package generaloss.mc24.client.resourcepack;

import generaloss.mc24.server.resourcepack.ResourceHandle;
import generaloss.mc24.server.resourcepack.ResourcePack;
import jpize.audio.util.AlMusic;
import jpize.util.res.Resource;

import java.util.Collection;

public class ResourceMusic extends ResourceHandle<String, AlMusic> {

    private final String ID;
    private final AlMusic music;

    public ResourceMusic(String ID, String path) {
        super(path);
        this.ID = ID;
        this.music = new AlMusic();
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public AlMusic getObject() {
        return music;
    }

    @Override
    public void load(ResourcePack pack) {
        final Resource resource = pack.get(super.getPath());
        music.load(resource);
    }

    @Override
    public void reload(Collection<ResourcePack> packs) {
        final Resource resource = super.getResourceFromPacks(packs, super.getPath());
        music.load(resource);
    }

    @Override
    public void dispose() {
        music.dispose();
    }

}