package generaloss.mc24.client.level.tesselation;

import jpize.gl.tesselation.GlPrimitive;
import jpize.gl.type.GlType;
import jpize.gl.vertex.GlVertAttr;
import jpize.util.array.FloatList;
import jpize.util.mesh.IndexedMesh;
import jpize.util.mesh.Mesh;

public class ChunkMesh {

    private final ChunkMeshCache cache;
    private final Mesh mesh;

    protected ChunkMesh(ChunkMeshCache cache) {
        this.cache = cache;
        this.mesh = new Mesh(
            new GlVertAttr(3, GlType.FLOAT) // position
        );
        this.mesh.setMode(GlPrimitive.QUADS);
    }

    public void setData(FloatList floats) {
        mesh.vertices().setData(floats.arrayTrimmed());
    }

    public void render() {
        mesh.render();
    }

    public void free() {
        cache.free(this);
    }

}
