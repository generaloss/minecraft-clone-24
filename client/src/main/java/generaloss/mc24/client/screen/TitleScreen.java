package generaloss.mc24.client.screen;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.registry.ClientRegistries;
import jpize.app.Jpize;
import jpize.audio.util.AlMusic;
import jpize.gl.Gl;
import jpize.gl.texture.GlFilter;
import jpize.gl.texture.Skybox;
import jpize.gl.texture.Texture2D;
import jpize.glfw.input.Key;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.font.Font;
import jpize.util.font.FontRenderOptions;
import jpize.util.postprocess.ScreenQuadMesh;
import jpize.util.postprocess.ScreenQuadShader;

public class TitleScreen extends IScreen {

    private final Texture2D overlayTexture;
    private final Skybox skybox;
    private final PerspectiveCamera camera;
    private final AlMusic music;
    private float yaw;
    private TextField serverAddressField;

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

    public void init() {
        this.serverAddressField = new TextField(10, 300, super.context().registries().getFont("default"));
        this.serverAddressField.setHint("localhost:22854");
    }

    @Override
    public void show() {
        Gl.clearColor(0F, 0F, 0F);
        music.play();
    }

    @Override
    public void hide() {
        music.pause();
        serverAddressField.disableInput();
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
        if(Key.ENTER.down()){
            final String[] serverAddress = serverAddressField.getText().split(":");
            try{
                context().connectSession(serverAddress[0], Integer.parseInt(serverAddress[1]));
            }catch(Exception e){
                serverAddressField.setText("Invalid address");
            }
        }
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

        // hints
        final Font font = super.context().registries().getFont("default");
        final FontRenderOptions fontOptions = font.getRenderOptions();

        float position = 10F;
        font.drawText("Press 'ENTER' for start.", 10F, position);
        position += font.getLineAdvanceScaled();
        font.drawText("'0', '1', '2' - changes resourcepack", 10F, position);
        position += font.getLineAdvanceScaled();
        font.drawText("'F11' - fullscreen", 10F, position);
        position += font.getLineAdvanceScaled();
        font.drawText("'K' - place stairs", 10F, position);
        position += font.getLineAdvanceScaled();
        font.drawText("'L' - place torches", 10F, position);

        // server address
        serverAddressField.render();
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(width, height);
    }

}
