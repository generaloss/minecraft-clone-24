package generaloss.mc24.client.renderer;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.client.meshing.chunk.ChunkMesh;
import generaloss.mc24.client.resources.ClientResources;
import generaloss.mc24.client.resources.handle.TextureAtlasHandle;
import generaloss.mc24.server.chunk.ChunkPos;
import jpize.context.Jpize;
import jpize.opengl.gl.Gl;
import jpize.opengl.shader.Shader;
import jpize.opengl.texture.Texture2D;
import jpize.context.input.Key;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.math.Mathc;
import jpize.util.math.matrix.Matrix4f;

public class LevelRenderer {

    private final WorldLevel level;
    private final Shader shader;
    private final Texture2D blockAtlasTexture;
    private final Matrix4f matrix;
    private float time;
    private boolean dayLightCycle = true;

    public LevelRenderer(Main context, WorldLevel level) {
        this.level = level;

        // resources
        this.shader = ClientResources.SHADERS.create("level_chunk", "shaders/chunk")
            .resource();

        this.blockAtlasTexture = ClientResources.ATLASES
            .create(new TextureAtlasHandle("blocks", "textures/blocks/", 512, 512))
            .resource().getTexture();

        // matrix
        this.matrix = new Matrix4f();
    }

    public void render(PerspectiveCamera camera) {
        // temporary daylight cycle control
        if(Key.F8.down())
            dayLightCycle = !dayLightCycle;
        if(dayLightCycle)
            time += Jpize.getDeltaTime();
        final float skylightFactor = Mathc.cos(time * 0.25) * 0.5F + 0.5F;

        // rendering
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
