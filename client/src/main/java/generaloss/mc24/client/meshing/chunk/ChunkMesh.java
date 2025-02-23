package generaloss.mc24.client.meshing.chunk;

import generaloss.mc24.client.meshing.block.BlockVertex;
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
        // attributes
        final GlVertAttr[] attributes = new GlVertAttr[BlockVertex.ATTRIBUTE_SIZE_ARRAY.length];
        for(int i = 0; i < attributes.length; i++)
            attributes[i] = new GlVertAttr(BlockVertex.ATTRIBUTE_SIZE_ARRAY[i], GlType.FLOAT);
        // mesh
        this.mesh = new Mesh(attributes);
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
