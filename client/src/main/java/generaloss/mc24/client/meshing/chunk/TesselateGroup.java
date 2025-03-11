package generaloss.mc24.client.meshing.chunk;

import generaloss.mc24.client.level.LevelChunk;

import java.util.*;
import java.util.stream.Collectors;

// chunk build sync by groups
public class TesselateGroup {

    private final List<LevelChunk> chunks;
    private final Map<LevelChunk, ChunkMesh> meshes;
    public int position;

    public TesselateGroup(List<LevelChunk> chunks) {
        this.chunks = chunks.stream().distinct().collect(Collectors.toList());
        this.meshes = new HashMap<>();
    }

    public TesselateGroup(LevelChunk... chunks) {
        this(List.of(chunks));
    }

    public void setMesh(LevelChunk chunk, ChunkMesh mesh, Runnable onComplete) {
        meshes.put(chunk, mesh);
        if(meshes.size() < chunks.size())
            return;
        
        onComplete.run();
        System.out.println("[INFO]: Tesselated " + chunks.size() + " chunks");
        // set meshes
        for(Map.Entry<LevelChunk, ChunkMesh> entry: meshes.entrySet()) {
            final LevelChunk entryChunk = entry.getKey();
            final ChunkMesh entryMesh = entry.getValue();
            entryChunk.freeMesh();
            if(entryMesh != null)
                entryChunk.setMesh(entryMesh);
        }
    }

    public boolean hasNext() {
        return (position < chunks.size());
    }
    
    public LevelChunk next() {
        return chunks.get(position++);
    }

}
