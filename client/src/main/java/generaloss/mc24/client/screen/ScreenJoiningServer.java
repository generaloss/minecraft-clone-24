package generaloss.mc24.client.screen;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.resources.ClientResources;
import generaloss.mc24.server.text.FormattedText;
import jpize.context.Jpize;
import jpize.opengl.gl.Gl;
import jpize.opengl.texture.Texture2D;
import jpize.context.input.Key;
import jpize.util.font.Font;
import jpize.util.math.vector.Vec2f;
import jpize.util.mesh.TextureBatch;
import jpize.util.region.Region;

public class ScreenJoiningServer extends Screen {

    public static final String SCREEN_ID = "joining_server";

    private final TextureBatch batch;
    private final Region regionBG;
    private final Texture2D background;
    private FormattedText status;

    public ScreenJoiningServer(Main context) {
        super(context, SCREEN_ID);

        this.batch = new TextureBatch();

        this.regionBG = new Region();
        this.background = ClientResources.TEXTURES.create("dirt", "textures/blocks/dirt.png")
            .resource();
    }

    public void setStatus(String status) {
        this.status = new FormattedText().text(status);
    }

    @Override
    public void show() {
        Gl.clearColor(0, 0, 0);
        this.setStatus("Connecting to the server..");
    }

    @Override
    public void update() {
        // exit to main_menu screen
        if(Key.ESCAPE.down()){
            super.context.disconnectSession();
            super.context.screens().setCurrent(ScreenMainMenu.SCREEN_ID);
        }
    }

    @Override
    public void render() {
        batch.setup();

        // background
        final int countY = 10;
        regionBG.u2 = countY * Jpize.getAspectRatio();
        regionBG.v2 = countY;
        batch.draw(background, regionBG, 0, 0, Jpize.getWidth(), Jpize.getHeight(), 1F, 1F, 1F, 0.35F);

        // status
        final Font font = ClientResources.FONTS.get("default").resource();
        final FormattedText text = status;
        final Vec2f textBounds = font.getTextBounds(text.getCachedText());
        final float textX = (Jpize.getWidth() - textBounds.x) * 0.5F;
        final float textY = (Jpize.getHeight() - textBounds.y) * 0.5F;
        context.formattedTextRenderer().draw(batch, text, textX, textY);

        batch.render(false);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

}
