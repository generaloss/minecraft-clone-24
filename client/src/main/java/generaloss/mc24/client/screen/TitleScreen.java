package generaloss.mc24.client.screen;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.registry.ClientRegistries;
import jpize.app.Jpize;
import jpize.audio.util.AlMusic;
import jpize.gl.texture.GlFilter;
import jpize.gl.texture.Skybox;
import jpize.gl.texture.Texture2D;
import jpize.glfw.input.Key;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.postprocess.ScreenQuadMesh;
import jpize.util.postprocess.ScreenQuadShader;

public class TitleScreen extends IScreen {

    private final Texture2D overlayTexture;
    private final Skybox skybox;
    private final PerspectiveCamera camera;
    private final AlMusic music;
    private float yaw;

    public TitleScreen(Main context) {
        super(context, "title");

        // resources
        final ClientRegistries resourceRegistry = context.registries();

        this.overlayTexture = resourceRegistry
            .registerTexture("menu_panorama_overlay", "textures/gui/title/panorama_overlay.png")
            .getObject()
            .setFilters(GlFilter.LINEAR);

        this.skybox = resourceRegistry
            .registerSkybox("menu_panorama_skybox", "textures/gui/title/panorama_%s.png")
            .getObject();

        this.music = resourceRegistry
            .registerMusic("menu_music", "music/beginning2.ogg")
            .getObject();

        // camera
        this.camera = new PerspectiveCamera(0.01F, 5F, 85F);
    }

    @Override
    public void show() {
        music.play();
    }

    @Override
    public void hide() {
        music.pause();
    }

    @Override
    public void update() {
        // music
        music.update();
        // camera
        yaw -= Jpize.getDeltaTime() * 3F;
        camera.rotation().setRotation(yaw, -15F, 0F);
        camera.update();
        // start session
        if(Key.P.down())
            context().connectLocalSession();
        // exit
        if(Key.ESCAPE.down())
            Jpize.exit();
    }

    @Override
    public void render() {
        // skybox
        skybox.render(camera);
        // overlay
        ScreenQuadShader.bind(overlayTexture);
        ScreenQuadMesh.render();
        // text
        super.context().font().drawText("Press 'P' for start.", 10, 10);
        super.context().font().drawText("'0', '1', '2' - changes resourcepack", 10, 80);
        super.context().font().drawText("'F11' - fullscreen", 10, 150);
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(width, height);
    }

}
