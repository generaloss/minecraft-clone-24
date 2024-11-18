package generaloss.mc24.client.level;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.level.mesh.ChunkMesh;
import generaloss.mc24.client.resource.ResourceDispatcher;
import generaloss.mc24.server.chunk.ChunkPos;
import jpize.gl.shader.Shader;
import jpize.gl.texture.Texture2D;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.math.matrix.Matrix4f;

import java.util.Collection;

public class LevelRenderer {

    private final WorldLevel level;
    private final Shader shader;
    private final Texture2D blockAtlas;
    private final Matrix4f matrix;

    public LevelRenderer(Main context, WorldLevel level) {
        this.level = level;

        // resources
        final ResourceDispatcher resources = context.resources();

        this.shader = context.resources().registerShader("level_chunk_shader", "/shaders/chunk")
            .resource();

        this.blockAtlas = context.resources().registerAtlas("block_atlas", "/textures/blocks", 256, 256)
            .registerAllInDirectory()
            .resource();

        // matrix
        this.matrix = new Matrix4f();
    }

    public void render(PerspectiveCamera camera) {
        shader.bind();
        shader.uniform("u_texture", blockAtlas);

        final Collection<LevelChunk> chunks = level.getChunks();
        for(LevelChunk chunk: chunks){
            final ChunkMesh mesh = chunk.mesh();
            if(mesh == null)
                continue;

            final ChunkPos position = chunk.getPosition();
            if(!position.isVisible(camera.frustum()))
                continue;

            matrix.set(camera.getCombined()).translate(position.getBlockX(), position.getBlockY(), position.getBlockZ());
            shader.uniform("u_combined", matrix);
            mesh.render();
        }
    }

}
