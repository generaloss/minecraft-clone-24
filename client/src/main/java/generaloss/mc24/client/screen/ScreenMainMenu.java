package generaloss.mc24.client.screen;

import generaloss.mc24.accountservice.network.Request;
import generaloss.mc24.accountservice.network.Response;
import generaloss.mc24.client.Main;
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
    private String serverInfo = "Server Info: (press 'Ctrl + I' to ping server)";
    private String accountStatus = "Account status: Not Logged In";
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
        this.nicknameField = new TextField(10, 650, ClientResources.FONTS.get("default").resource());
        this.nicknameField.setHint("nickname");
        this.nicknameField.setFocused(true);

        this.passwordField = new TextField(10, 600, ClientResources.FONTS.get("default").resource());
        this.passwordField.setHint("password");

        this.serverAddressField = new TextField(10, 550, ClientResources.FONTS.get("default").resource());
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
                accountStatus = "Register status: Registering...";
                final Response response = Request.sendCreateAccount(SharedConstants.ACCOUNTS_HOST, nicknameField.getText(), passwordField.getText());
                accountStatus = "Register status: " + response.getCode() + ", " + response.readString() + " (Not Logged In)";
            });
        }
        // login
        if(Key.LCTRL.pressed() && Key.L.down()) {
            executors.execute(() -> {
                final String nickname = nicknameField.getText();
                accountStatus = "Login status: Logging...";
                final Response response = Request.sendLogin(SharedConstants.ACCOUNTS_HOST, nickname, passwordField.getText());
                if(response.getCode().noError()){
                    final AccountSession session = new AccountSession(response.readUUID(), nickname);
                    super.context.setSession(session);
                    System.out.println("[INFO]: Logged in as '" + nickname + "'");
                }
                accountStatus = "Login status: " + response.getCode() + ", " + response.readString();
            });
        }
        // join
        if(Key.ENTER.down()){
            final String[] serverAddress = serverAddressField.getText().split(":");
            try{
                final int port = Integer.parseInt(serverAddress[1]);
                context.connectSession(serverAddress[0], port);
            }catch(NumberFormatException | ArrayIndexOutOfBoundsException | IllegalStateException e){
                serverInfo = "Server Info: Invalid address (press 'Ctrl + I' to ping server)";
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
                serverInfo = "Server Info: Invalid address (press 'Ctrl + I' to ping server)";
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
        serverInfo = "Server info: " + motd + ", " + version + ", " + ping + "ms.";
    }

    FormattedText t = new FormattedText()
        .text("I ")
        .style(TextFormatting.ITALIC).text("Want ").style()
        .text("Pizza")
        .color(TextColor.RED).text("\n").text("What?\n").text("What.\n")
        .style(TextFormatting.BOLD).color(TextColor.DARK_GREEN).text("Bold text");

    @Override
    public void render() {
        // skybox
        skybox.render(camera);

        // overlay
        RenderQuad.instance().render(overlayTexture);

        // font init
        final Font font = ClientResources.FONTS.get("default").resource();
        final FontRenderOptions fontOptions = font.getRenderOptions();

        batch.setup();

        // hints
        // float position = 10F;
        // font.getRenderOptions().color().setRGB(0xAAAAAA);
        // font.drawText(batch, "Press 'ENTER' to join the server.", 10F, position);
        // position += font.getLineAdvanceScaled();
        // font.drawText(batch, "'F1', 'F2', 'F3' - changes resourcepack", 10F, position);
        // position += font.getLineAdvanceScaled();
        // font.drawText(batch, "'F11' - fullscreen", 10F, position);
        // position += font.getLineAdvanceScaled();
        // font.drawText(batch, "Numbers - select block (ingame)", 10F, position);
        // position += font.getLineAdvanceScaled();
        // font.drawText(batch, "'Ctrl' - sprint (ingame)", 10F, position);
        // position += font.getLineAdvanceScaled();
        // font.drawText(batch, "'Ctrl + R' - register account", 10F, position);
        // position += font.getLineAdvanceScaled();
        // font.drawText(batch, "'Ctrl + L' - login account", 10F, position);
        // position += font.getLineAdvanceScaled();
        // font.drawText(batch, "'Ctrl + S' - paste server IP", 10F, position);
        // position += font.getLineAdvanceScaled();
        // font.drawText(batch, "'Ctrl + ESCAPE' - quit / to main menu", 10F, position);
        // position += font.getLineAdvanceScaled();
        // font.drawText(batch, "'F5'/'F6' - enable/disable Ambient Occlusion", 10F, position);
        // position += font.getLineAdvanceScaled();
        // font.drawText(batch, "'F8' - enable/disable daylight cycle", 10F, position);
        // position += font.getLineAdvanceScaled();
        // font.getRenderOptions().color().reset();
        // font.drawText(batch, serverInfo, 10F, position);
        // position += font.getLineAdvanceScaled();
        // font.drawText(batch, accountStatus, 10F, position);

        // server address
        serverAddressField.render(batch);
        nicknameField.render(batch);
        passwordField.render(batch);

        context.formattedTextRenderer().draw(batch, t, 100, 500);
        batch.render();
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(width, height);
    }

}
