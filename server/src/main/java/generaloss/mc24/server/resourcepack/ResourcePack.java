package generaloss.mc24.server.resourcepack;

import jpize.util.Disposable;
import jpize.util.Utils;
import jpize.util.res.Resource;
import jpize.util.res.ZipResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

public class ResourcePack implements Disposable {

    public static final String DIRECTORY = "./resource-packs/";

    private final String ID;
    private final ZipFile zipFile;
    private final Map<String, ZipResource> resourceMap;

    public ResourcePack(String filename) {
        try{
            this.ID = filename;
            this.zipFile = new ZipFile(DIRECTORY + filename);
            final ZipResource[] resources = Resource.zip(zipFile);

            this.resourceMap = new HashMap<>();
            for(ZipResource resource : resources)
                resourceMap.put(resource.path(), resource);

        }catch(IOException e){
            throw new IllegalArgumentException("Could not load resource pack: '" + filename + "'");
        }
    }

    public String getID() {
        return ID;
    }

    public ZipResource getResource(String path) {
        return resourceMap.get(path);
    }

    @Override
    public void dispose() {
        Utils.close(zipFile);
    }

}
