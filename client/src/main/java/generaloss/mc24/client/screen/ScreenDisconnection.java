package generaloss.mc24.client.screen;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.resources.ClientResources;
import generaloss.mc24.server.text.FormattedText;
import jpize.app.Jpize;
import jpize.opengl.gl.Gl;
import jpize.opengl.texture.Texture2D;
import jpize.io.input.Key;
import jpize.util.font.Font;
import jpize.util.math.vector.Vec2f;
import jpize.util.mesh.TextureBatch;
import jpize.util.region.Region;

public class ScreenDisconnection extends Screen {

    public static final String SCREEN_ID = "disconnection";

    private final TextureBatch batch;
    private final Region regionBG;
    private FormattedText message;

    public ScreenDisconnection(Main context) {
        super(context, SCREEN_ID);

        this.batch = new TextureBatch();

        this.regionBG = new Region();
    }

    public void setMessage(String message) {
        this.message = new FormattedText().text("Disconnection: '" + message + "'");
    }

    @Override
    public void show() {
        Gl.clearColor(0, 0, 0);
    }

    @Override
    public void update() {
        // exit to main_menu screen
        if(Key.ESCAPE.down())
            super.context.screens().setCurrent(ScreenMainMenu.SCREEN_ID);
    }

    @Override
    public void render() {
        batch.setup();

        // background
        final int countY = 10;
        regionBG.u2 = countY * Jpize.getAspectRatio();
        regionBG.v2 = countY;

        final Texture2D background = ClientResources.TEXTURES.get("dirt").resource();
        batch.draw(background, regionBG, 0, 0, Jpize.getWidth(), Jpize.getHeight(), 1F, 1F, 1F, 0.35F);

        // message
        final Font font = ClientResources.FONTS.get("default").resource();
        final FormattedText text = message;
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