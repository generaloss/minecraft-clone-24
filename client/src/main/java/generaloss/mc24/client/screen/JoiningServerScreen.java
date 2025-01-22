package generaloss.mc24.client.screen;

import generaloss.mc24.client.Main;
import jpize.app.Jpize;
import jpize.gl.Gl;
import jpize.gl.texture.Texture2D;
import jpize.glfw.input.Key;
import jpize.util.font.Font;
import jpize.util.math.vector.Vec2f;
import jpize.util.mesh.TextureBatch;
import jpize.util.region.Region;

public class JoiningServerScreen extends Screen {

    public static final String SCREEN_ID = "joining_server";

    private final TextureBatch batch;
    private final Region regionBG;
    private final Texture2D background;
    private String status;

    public JoiningServerScreen(Main context) {
        super(context, SCREEN_ID);

        this.batch = new TextureBatch();

        this.regionBG = new Region();
        this.background = super.context.registries().TEXTURES
            .register("crosshair", "textures/blocks/dirt.png")
            .getObject();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void show() {
        Gl.clearColor(0, 0, 0);
        this.setStatus("Joining server...");
    }

    @Override
    public void update() {
        // exit to main_menu screen
        if(Key.ESCAPE.down()){
            super.context.disconnectSession();
            super.context.screens().show(MainMenuScreen.SCREEN_ID);
        }
    }

    @Override
    public void render() {
        batch.setup();

        // background
        final int countY = 10;
        regionBG.u2 = countY * Jpize.window().getAspectRatio();
        regionBG.v2 = countY;
        //batch.draw(background, regionBG, 0, 0, Jpize.getWidth(), Jpize.getHeight(), 1F, 1F, 1F, 0.35F);

        // status
        final Font font = super.context.registries().FONTS.get("default");
        final String text = status;
        final Vec2f textBounds = font.getTextBounds(text);
        final float textX = (Jpize.getWidth() - textBounds.x) * 0.5F;
        final float textY = (Jpize.getHeight() - textBounds.y) * 0.5F;
        font.drawText(batch, text, textX, textY);

        batch.render(false);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

}
