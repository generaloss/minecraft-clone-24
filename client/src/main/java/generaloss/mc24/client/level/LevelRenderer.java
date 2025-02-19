package generaloss.mc24.client.level;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.chunkmesh.ChunkMesh;
import generaloss.mc24.client.resource.ClientResources;
import generaloss.mc24.client.resource.TextureAtlasHandle;
import generaloss.mc24.server.chunk.ChunkPos;
import jpize.app.Jpize;
import jpize.gl.Gl;
import jpize.gl.shader.Shader;
import jpize.gl.texture.Texture2D;
import jpize.glfw.input.Key;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.math.Mathc;
import jpize.util.math.matrix.Matrix4f;
import jpize.util.math.vector.Vec3f;

public class LevelRenderer {

    public static final Vec3f LIGHT_DIRECTION = new Vec3f(-0.3F, -1F, -0.7F);

    private final WorldLevel level;
    private final Shader shader;
    private final Texture2D blockAtlasTexture;
    private final Matrix4f matrix;
    private float time;
    private boolean dayLightCycle = true;

    public LevelRenderer(Main context, WorldLevel level) {
        this.level = level;

        // registries
        this.shader = ClientResources.SHADERS.create("level_chunk", "shaders/chunk")
            .resource();

        this.blockAtlasTexture = ClientResources.ATLASES
            .create(new TextureAtlasHandle("blocks", "textures/blocks/", 512, 512))
            .resource().getTexture();

        // matrix
        this.matrix = new Matrix4f();
    }

    public void render(PerspectiveCamera camera) {
        if(Key.F8.down())
            dayLightCycle = !dayLightCycle;
        if(dayLightCycle)
            time += Jpize.getDeltaTime();
        final float skylightFactor = Mathc.cos(time * 0.75) * 0.5F + 0.5F;

        Gl.clearColor(0.4 * skylightFactor, 0.6 * skylightFactor, 0.9 * skylightFactor);

        shader.bind();
        shader.uniform("u_texture", blockAtlasTexture);
        shader.uniform("u_skylightFactor", skylightFactor);

        level.forEachChunk(chunk -> {
            final ChunkMesh mesh = chunk.mesh();
            if(mesh == null)
                return true;

            final ChunkPos position = chunk.position();
            if(!position.isVisible(camera.frustum()))
                return true;

            matrix.set(camera.getCombined()).translate(position.getBlockX(), position.getBlockY(), position.getBlockZ());
            shader.uniform("u_combined", matrix);
            mesh.render();
            return true;
        });
    }

}
