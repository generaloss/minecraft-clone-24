package generaloss.mc24.client.render;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.client.level.tesselation.ChunkMesh;
import generaloss.mc24.client.session.SessionScreen;
import jpize.gl.shader.Shader;

public class LevelRenderer {

    private final WorldLevel level;
    private final SessionScreen session;
    private final Shader shader;

    public LevelRenderer(Main context, SessionScreen session, WorldLevel level) {
        this.level = level;
        this.session = session;
        this.shader = context.resources().registerShader("level_chunk_shader", "/shaders/chunk")
            .resource();
    }

    public void render() {
        shader.bind();
        shader.uniform("u_combined",  session.player().camera().getCombined());

        final ChunkMesh mesh = level.getChunkMesh();
        mesh.render();
    }

}
