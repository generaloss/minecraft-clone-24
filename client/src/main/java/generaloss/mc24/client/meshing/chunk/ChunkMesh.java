package generaloss.mc24.client.meshing.chunk;

import generaloss.mc24.client.meshing.block.BlockVertex;
import jpize.opengl.tesselation.GLPrimitive;
import jpize.opengl.type.GLType;
import jpize.opengl.vertex.GLVertAttr;
import jpize.util.Disposable;
import jpize.util.mesh.Mesh;

public class ChunkMesh implements Disposable {

    private final ChunkMeshCache cache;
    private final Mesh mesh;

    protected ChunkMesh(ChunkMeshCache cache) {
        this.cache = cache;
        // attributes
        final GLVertAttr[] attributes = new GLVertAttr[BlockVertex.ATTRIBUTE_SIZE_ARRAY.length];
        for(int i = 0; i < attributes.length; i++)
            attributes[i] = new GLVertAttr(BlockVertex.ATTRIBUTE_SIZE_ARRAY[i], GLType.FLOAT);
        // mesh
        this.mesh = new Mesh(attributes);
        this.mesh.setMode(GLPrimitive.QUADS);
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
