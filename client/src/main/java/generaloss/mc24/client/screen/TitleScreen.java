package generaloss.mc24.client.screen;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.registry.ClientRegistries;
import generaloss.mc24.server.network.packet2s.ServerInfoRequestPacket2S;
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
    private String serverInfo = "Server Info: (press 'I' to ping server)";
    private String disconnectMessage = "";

    public TitleScreen(Main context) {
        super(context, "title");

        // resources
        final ClientRegistries resourceRegistry = context.registries();

        this.overlayTexture = resourceRegistry.TEXTURES
            .register("menu_panorama_overlay", "textures/gui/title/panorama_overlay.png")
            .getObject()
            .setFilters(GlFilter.LINEAR);

        this.skybox = resourceRegistry.SKYBOXES
            .register("menu_panorama_skybox", "textures/gui/title/panorama_%s.png")
            .getObject();

        this.music = (AlMusic) resourceRegistry.MUSICS
            .register("menu_music", "music/beginning2.ogg")
            .getObject()
            .setLooping(true);

        // camera
        this.camera = new PerspectiveCamera(0.01F, 5F, 85F);
    }

    public void init() {
        this.serverAddressField = new TextField(10, 300, super.context().registries().FONTS.get("default"));
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
        camera.rotation().setRotation(yaw, 15F, 0F);
        camera.update();
        // start session
        if(Key.ENTER.down()){
            final String[] serverAddress = serverAddressField.getText().split(":");
            try{
                final int port = Integer.parseInt(serverAddress[1]);
                context().connectSession(serverAddress[0], port);
            }catch(NumberFormatException | ArrayIndexOutOfBoundsException | IllegalStateException e){
                serverInfo = "Server Info: Invalid address (press 'I' to ping server)";
            }
        }
        if(Key.I.down()) {
            final String[] serverAddress = serverAddressField.getText().split(":");
            try{
                final int port = Integer.parseInt(serverAddress[1]);
                super.context().connection().connect(serverAddress[0], port);
                super.context().connection().sendPacket(new ServerInfoRequestPacket2S(
                        System.currentTimeMillis()
                ));
            }catch(NumberFormatException | ArrayIndexOutOfBoundsException | IllegalStateException e){
                serverInfo = "Server Info: Invalid address (press 'I' to ping server)";
            }
        }
        // exit
        if(Key.ESCAPE.down())
            Jpize.exit();
    }

    public void onServerInfo(String motd, String version, long ping) {
        serverInfo = "Server info: " + motd + ", " + version + ", " + ping + "ms.";
    }

    public void onDisconnect(String message) {
        disconnectMessage = "Disconnection: " + message;
    }

    @Override
    public void render() {
        // skybox
        skybox.render(camera);

        // overlay
        ScreenQuadShader.bind(overlayTexture);
        ScreenQuadMesh.render();

        // hints
        final Font font = super.context().registries().FONTS.get("default");
        final FontRenderOptions fontOptions = font.getRenderOptions();

        float position = 10F;
        font.drawText("Press 'ENTER' for start.", 10F, position);
        position += font.getLineAdvanceScaled();
        font.drawText("'F1', 'F2', 'F3' - changes resourcepack", 10F, position);
        position += font.getLineAdvanceScaled();
        font.drawText("'F11' - fullscreen", 10F, position);
        position += font.getLineAdvanceScaled();
        font.drawText("'K' - place stairs (ingame)", 10F, position);
        position += font.getLineAdvanceScaled();
        font.drawText("'L' - place torches (ingame)", 10F, position);
        position += font.getLineAdvanceScaled();
        font.drawText("'ESCAPE' - quit / to main menu", 10F, position);
        position += font.getLineAdvanceScaled();
        font.drawText(serverInfo, 10F, position);
        position += font.getLineAdvanceScaled();
        font.drawText(disconnectMessage, 10F, position);

        // server address
        serverAddressField.render();
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(width, height);
    }

}
