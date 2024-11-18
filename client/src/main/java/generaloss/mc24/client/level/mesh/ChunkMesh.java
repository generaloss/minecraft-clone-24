package generaloss.mc24.client.level.mesh;

import jpize.gl.tesselation.GlPrimitive;
import jpize.gl.type.GlType;
import jpize.gl.vertex.GlVertAttr;
import jpize.util.Disposable;
import jpize.util.mesh.Mesh;

public class ChunkMesh implements Disposable {

    private final ChunkMeshCache cache;
    private final Mesh mesh;

    protected ChunkMesh(ChunkMeshCache cache) {
        this.cache = cache;
        this.mesh = new Mesh(
            new GlVertAttr(3, GlType.FLOAT), // position
            new GlVertAttr(2, GlType.FLOAT), // texcoord
            new GlVertAttr(4, GlType.FLOAT)  // color
        );
        this.mesh.setMode(GlPrimitive.QUADS);
    }

    public void setData(float[] floats) {
        mesh.vertices().setData(floats);
    }

    public void render() {
        mesh.render();
    }

    public void free() {
        cache.free(this);
    }

    @Override
    public void dispose() {
        mesh.dispose();
    }

}
