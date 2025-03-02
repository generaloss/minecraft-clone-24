package generaloss.mc24.client.screen;

import generaloss.mc24.accountservice.network.Request;
import generaloss.mc24.accountservice.network.Response;
import generaloss.mc24.client.Main;
import generaloss.mc24.client.renderer.FormattedTextRenderer;
import generaloss.mc24.client.resources.ClientResources;
import generaloss.mc24.client.utils.TextField;
import generaloss.mc24.server.SharedConstants;
import generaloss.mc24.server.network.AccountSession;
import generaloss.mc24.server.network.packet2s.ServerInfoRequestPacket2S;
import generaloss.mc24.server.text.FormattedText;
import generaloss.mc24.server.text.formatting.TextColor;
import generaloss.mc24.server.text.formatting.TextFormatting;
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
import jpize.util.mesh.TextureBatch;
import jpize.util.postprocess.RenderQuad;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScreenMainMenu extends Screen {

    public static final String SCREEN_ID = "main_menu";

    private final Texture2D overlayTexture;
    private final Skybox skybox;
    private final PerspectiveCamera camera;
    private final AlMusic music;
    private float yaw;

    private final TextureBatch batch;
    private TextField serverAddressField;
    private TextField nicknameField;
    private TextField passwordField;
    private FormattedText serverInfo = new FormattedText().color(TextColor.DARK_GRAY).text("Server Info: (press ").color(TextColor.WHITE).style(TextFormatting.ITALIC).text("Ctrl + I").color(TextColor.GRAY).style().text(" to ping server)");
    private FormattedText accountStatus = new FormattedText().color(TextColor.DARK_GRAY).text("Account status: ").color(TextColor.DARK_RED).text("Not Logged In");
    private final ExecutorService executors;

    public ScreenMainMenu(Main context) {
        super(context, ScreenMainMenu.SCREEN_ID);

        this.batch = new TextureBatch();

        // resources
        this.overlayTexture = ClientResources.TEXTURES
            .create("menu_panorama_overlay", "textures/gui/title/panorama_overlay.png")
            .resource()
            .setFilters(GlFilter.LINEAR);

        this.skybox = ClientResources.SKYBOXES
            .create("menu_panorama_skybox", "textures/gui/title/panorama_%s.png")
            .resource();

        this.music = (AlMusic) ClientResources.MUSICS
            .create("menu_music", "music/beginning2.ogg")
            .resource()
            .setLooping(true)
            .setGain(0.1);

        // camera
        this.camera = new PerspectiveCamera(0.01F, 5F, 85F);
        // executor service
        this.executors = Executors.newWorkStealingPool();
    }

    public void init() {
        nicknameField = new TextField(10, 150, ClientResources.FONTS.get("default").resource());
        nicknameField.setHint("nickname");

        passwordField = new TextField(10, 220, ClientResources.FONTS.get("default").resource());
        passwordField.setHint("password");

        serverAddressField = new TextField(10, 290, ClientResources.FONTS.get("default").resource());
        serverAddressField.setHint("localhost:22854");
    }

    @Override
    public void show() {
        Gl.clearColor(0F, 0F, 0F);
        music.play();
        nicknameField.setFocused(true);
    }

    @Override
    public void hide() {
        music.pause();
        nicknameField.setFocused(false);
        passwordField.setFocused(false);
        serverAddressField.setFocused(false);
    }

    @Override
    public void update() {
        // music
        music.update();
        // camera
        yaw += Jpize.getDeltaTime() * 3F;
        camera.rotation().set(yaw, -15F, 0F);
        camera.update();
        // register
        if(Key.LCTRL.pressed() && Key.R.down()) {
            executors.execute(() -> {
                accountStatus = new FormattedText().color(TextColor.DARK_GRAY).text("Register status: ").color(TextColor.WHITE).text("Registering...");
                final Response response = Request.sendCreateAccount(SharedConstants.ACCOUNTS_HOST, nicknameField.getText(), passwordField.getText());
                accountStatus = new FormattedText().color(TextColor.DARK_GRAY).text("Register status: " + response.getCode() + ", " + response.readString()).color(TextColor.DARK_RED).text(" (Not Logged In)");
            });
        }
        // login
        if(Key.LCTRL.pressed() && Key.L.down()) {
            executors.execute(() -> {
                final String nickname = nicknameField.getText();
                accountStatus = new FormattedText().color(TextColor.DARK_GRAY).text("Login status: ").color(TextColor.WHITE).text("Logging...");
                final Response response = Request.sendLogin(SharedConstants.ACCOUNTS_HOST, nickname, passwordField.getText());
                if(response.getCode().noError()){
                    final AccountSession session = new AccountSession(response.readUUID(), nickname);
                    super.context.setSession(session);
                    System.out.println("[INFO]: Logged in as '" + nickname + "'");
                }
                accountStatus = new FormattedText().color(TextColor.DARK_GRAY).text("Login status: ").color(TextColor.WHITE).text(response.getCode() + ", " + response.readString());
            });
        }
        // join
        if(Key.ENTER.down()){
            final String[] serverAddress = serverAddressField.getText().split(":");
            try{
                final int port = Integer.parseInt(serverAddress[1]);
                context.connectSession(serverAddress[0], port);
            }catch(NumberFormatException | ArrayIndexOutOfBoundsException | IllegalStateException e){
                serverInfo = new FormattedText().text("Server Info: Invalid address (press 'Ctrl + I' to ping server)");
            }
        }
        // ping server
        if(Key.LCTRL.pressed() && Key.I.down()) {
            final String[] serverAddress = serverAddressField.getText().split(":");
            try{
                final int port = Integer.parseInt(serverAddress[1]);
                super.context.network().connect(serverAddress[0], port);
                super.context.network().sendPacket(new ServerInfoRequestPacket2S(System.currentTimeMillis()));
            }catch(NumberFormatException | ArrayIndexOutOfBoundsException | IllegalStateException e){
                serverInfo = new FormattedText().text("Server Info: Invalid address (press 'Ctrl + I' to ping server)");
            }
        }
        // server ip
        if(Key.LCTRL.pressed() && Key.S.down())
            serverAddressField.setText("mineclone.ignorelist.com:22854");
        // exit
        if(Key.LCTRL.pressed() && Key.ESCAPE.down())
            Jpize.exit();
        // tab fields
        if(Key.TAB.down()) {
            if(nicknameField.isFocused()){
                nicknameField.setFocused(false);
                passwordField.setFocused(true);

            }else if(passwordField.isFocused()){
                passwordField.setFocused(false);
                serverAddressField.setFocused(true);

            }else if(serverAddressField.isFocused()){
                serverAddressField.setFocused(false);
                nicknameField.setFocused(true);
            }
        }
    }

    public void onServerInfo(String motd, String version, long ping) {
        serverInfo = new FormattedText().text("Server info: " + motd + ", " + version + ", " + ping + "ms.");
    }

    @Override
    public void render() {
        // skybox
        skybox.render(camera);

        // overlay
        RenderQuad.instance().render(overlayTexture);

        // font init
        final Font font = ClientResources.FONTS.get("default").resource();
        final FontRenderOptions fontOptions = font.getOptions();
        final FormattedTextRenderer textRenderer = context.formattedTextRenderer();

        batch.setup();

        // hints
        float position = font.getLineAdvanceScaled();
        font.getOptions().color().setRGB(0xAAAAAA);
        textRenderer.draw(batch, new FormattedText().color(TextColor.GRAY).text("Press ").color(TextColor.WHITE).style(TextFormatting.ITALIC).text("ENTER").color(TextColor.GRAY).style().text(" to join the server."), 10F, position);
        position += font.getLineAdvanceScaled();
        textRenderer.draw(batch, new FormattedText().color(TextColor.WHITE).style(TextFormatting.ITALIC).text("F1; F2; F3").color(TextColor.GRAY).style().text(" - changes resourcepack"), 10F, position);
        position += font.getLineAdvanceScaled();
        textRenderer.draw(batch, new FormattedText().color(TextColor.WHITE).style(TextFormatting.ITALIC).text("F11").color(TextColor.GRAY).style().text(" - fullscreen"), 10F, position);
        position += font.getLineAdvanceScaled();
        textRenderer.draw(batch, new FormattedText().color(TextColor.WHITE).style(TextFormatting.ITALIC).text("Numbers").color(TextColor.GRAY).style().text(" - select block (ingame)"), 10F, position);
        position += font.getLineAdvanceScaled();
        textRenderer.draw(batch, new FormattedText().color(TextColor.WHITE).style(TextFormatting.ITALIC).text("Ctrl").color(TextColor.GRAY).style().text(" - sprint (ingame)"), 10F, position);
        position += font.getLineAdvanceScaled();
        textRenderer.draw(batch, new FormattedText().color(TextColor.WHITE).style(TextFormatting.ITALIC).text("Ctrl + R").color(TextColor.GRAY).style().text(" - register account"), 10F, position);
        position += font.getLineAdvanceScaled();
        textRenderer.draw(batch, new FormattedText().color(TextColor.WHITE).style(TextFormatting.ITALIC).text("Ctrl + L").color(TextColor.GRAY).style().text(" - login account"), 10F, position);
        position += font.getLineAdvanceScaled();
        textRenderer.draw(batch, new FormattedText().color(TextColor.WHITE).style(TextFormatting.ITALIC).text("Ctrl + S").color(TextColor.GRAY).style().text(" - paste server IP"), 10F, position);
        position += font.getLineAdvanceScaled();
        textRenderer.draw(batch, new FormattedText().color(TextColor.WHITE).style(TextFormatting.ITALIC).text("Ctrl + ESCAPE").color(TextColor.GRAY).style().text(" - quit / to main menu"), 10F, position);
        position += font.getLineAdvanceScaled();
        textRenderer.draw(batch, new FormattedText().color(TextColor.WHITE).style(TextFormatting.ITALIC).text("F5 / F6").color(TextColor.GRAY).style().text(" - enable / disable Ambient Occlusion"), 10F, position);
        position += font.getLineAdvanceScaled();
        textRenderer.draw(batch, new FormattedText().color(TextColor.WHITE).style(TextFormatting.ITALIC).text("F8").color(TextColor.GRAY).style().text(" - enable/disable daylight cycle"), 10F, position);
        position += font.getLineAdvanceScaled();
        font.getOptions().color().reset();
        textRenderer.draw(batch, serverInfo, 10F, position);
        position += font.getLineAdvanceScaled();
        textRenderer.draw(batch, accountStatus, 10F, position);

        // server address
        serverAddressField.render(batch);
        nicknameField.render(batch);
        passwordField.render(batch);

        batch.render();
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(width, height);
    }

}
