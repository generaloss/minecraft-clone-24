package generaloss.mc24.client.level;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.chunkmesh.ChunkMesh;
import generaloss.mc24.client.resource.ClientResources;
import generaloss.mc24.client.resource.TextureAtlasHandle;
import generaloss.mc24.server.chunk.ChunkPos;
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

        // registries
        this.shader = ClientResources.SHADERS.load("level_chunk", "shaders/chunk")
            .resource();

        this.blockAtlas = ClientResources.ATLASES.load(new TextureAtlasHandle("blocks", "textures/blocks/", 256, 256))
            .resource().getTexture();

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

            final ChunkPos position = chunk.position();
            if(!position.isVisible(camera.frustum()))
                continue;

            matrix.set(camera.getCombined()).translate(position.getBlockX(), position.getBlockY(), position.getBlockZ());
            shader.uniform("u_combined", matrix);
            mesh.render();
        }
    }

}
