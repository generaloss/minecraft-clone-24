package generaloss.mc24.client.level.renderer;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.client.resource.ResourcesRegistryClient;
import generaloss.mc24.server.chunk.ChunkPos;
import jpize.gl.Gl;
import jpize.gl.shader.Shader;
import jpize.gl.texture.Texture2D;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.math.matrix.Matrix4f;
import jpize.util.math.vector.Vec3f;

import java.util.Collection;

public class LevelRenderer {

    public static final Vec3f LIGHT_DIRECTION = new Vec3f(-0.3F, -1F, -0.7F);

    private final WorldLevel level;
    private final Shader shader;
    private final Texture2D blockAtlas;
    private final Matrix4f matrix;

    public LevelRenderer(Main context, WorldLevel level) {
        this.level = level;

        // resources
        final ResourcesRegistryClient resources = context.resources();

        this.shader = context.resources().registerShader("level_chunk_shader", "/resources/shaders/chunk")
            .resource();

        this.blockAtlas = context.resources().registerAtlas("block_atlas", "/resources/textures/blocks", 256, 256)
            .registerAllInDirectory()
            .resource();

        // matrix
        this.matrix = new Matrix4f();

        // options
        Gl.clearColor(0.4, 0.7, 0.9);
    }

    public void render(PerspectiveCamera camera) {
        shader.bind();
        shader.uniform("u_texture", blockAtlas);
        shader.uniform("u_lightDirection", LIGHT_DIRECTION);

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
