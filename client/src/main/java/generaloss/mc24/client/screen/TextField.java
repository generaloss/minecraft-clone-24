package generaloss.mc24.client.screen;

import jpize.app.Jpize;
import jpize.gl.tesselation.GlScissor;
import jpize.glfw.input.MouseBtn;
import jpize.util.font.Font;
import jpize.util.input.TextInput;
import jpize.util.math.Intersector;

public class TextField {

    private final int x;
    private final int y;
    private final Font font;
    private String hint;
    private final TextInput input;
    private final GlScissor<String> scissor;

    public TextField(int x, int y, Font font) {
        this.x = x;
        this.y = y;
        this.font = font;
        this.input = new TextInput().setMaxLines(1);
        this.scissor = new GlScissor<>();
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getText() {
        final String text = input.makeString();
        return (text.isEmpty() ? hint : text);
    }

    public void setText(String text) {
        input.setLine(text);
    }

    public boolean isInputEnabled() {
        return input.isEnabled();
    }

    public void disableInput() {
        input.disable();
    }

    public void render() {
        final float height = font.getLineAdvanceScaled();
        final float width = font.getTextWidth("W".repeat(30));

        if(MouseBtn.pressedAny(MouseBtn.values())) {
            input.enable(Intersector.isPointOnRect(Jpize.getX(), Jpize.getY(), x, y, width, height));
        }

        scissor.put("scissor", x, y, width, height);
        scissor.apply();

        final String text = input.makeString();
        if(text.isEmpty()) {
            font.getRenderOptions().color().set(0.7F);
            font.drawText(hint, x, y);
        }else{
            font.drawText(text, x, y);
        }
        font.getRenderOptions().color().reset();

        scissor.remove("scissor");
        scissor.apply();
    }

}
