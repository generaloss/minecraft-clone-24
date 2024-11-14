package generaloss.mc24.client.menu;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.resource.ResourceDispatcher;
import generaloss.mc24.client.screen.IScreen;
import jpize.app.Jpize;
import jpize.audio.util.AlMusic;
import jpize.gl.texture.GlFilter;
import jpize.gl.texture.Skybox;
import jpize.gl.texture.Texture2D;
import jpize.glfw.input.Key;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.postprocess.ScreenQuadMesh;
import jpize.util.postprocess.ScreenQuadShader;

public class MainMenuScreen extends IScreen {

    private final Texture2D overlayTexture;
    private final Skybox skybox;
    private final PerspectiveCamera camera;
    private final AlMusic music;
    private float yaw;

    public MainMenuScreen(Main context) {
        super(context, "main_menu");

        // resources
        final ResourceDispatcher resources = super.context().resources();

        this.overlayTexture = resources
            .registerTexture("menu_panorama_overlay", "/textures/panorama/overlay.png")
            .resource()
            .setFilters(GlFilter.LINEAR);

        this.skybox = resources
            .registerSkybox("menu_panorama_skybox", "/textures/panorama/skybox_%s.png")
            .resource();

        this.music = resources
            .registerMusic("menu_music", "/music/beginning2.ogg")
            .resource();

        // camera
        this.camera = new PerspectiveCamera(0.01F, 5F, 85F);
    }

    @Override
    public void init() { }

    @Override
    public void show() {
        System.out.println("Show main menu screen");
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
        yaw -= Jpize.getDT() * 3F;
        camera.rotation().setRotation(yaw, -15F, 0F);
        camera.update();
        // start session
        if(Key.P.down())
            context().startSession();
    }

    @Override
    public void render() {
        // skybox
        skybox.render(camera);
        // overlay
        ScreenQuadShader.bind(overlayTexture);
        ScreenQuadMesh.render();
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(width, height);
    }

}
