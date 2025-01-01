package generaloss.mc24.client.screen;

import jpize.app.Jpize;
import jpize.glfw.input.MouseBtn;
import jpize.util.font.Font;
import jpize.util.input.TextInput;
import jpize.util.math.Intersector;
import jpize.util.mesh.TextureBatch;

public class TextField {

    private final int x;
    private final int y;
    private final Font font;
    private String hint;
    private final TextInput input;
    private float time;

    public TextField(int x, int y, Font font) {
        this.x = x;
        this.y = y;
        this.font = font;
        this.input = new TextInput().setMaxLines(1);
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

    public void render(TextureBatch batch) {
        final float height = font.getLineAdvanceScaled();
        final float width = font.getTextWidth("W".repeat(30));

        batch.drawBlackRect(x, y, width, height, 0.5F);

        if(MouseBtn.pressedAny(MouseBtn.values())){
            input.enable(Intersector.isPointOnRect(Jpize.getX(), Jpize.getY(), x, y, width, height));
            time = 0;
        }

        // scissor.put("scissor", x, y, width, height);
        // scissor.apply();

        final String text = input.makeString();
        if(text.isEmpty()) {
            font.getRenderOptions().color().set(0.7F);
            font.drawText(batch, hint, x, y);
        }else{
            font.drawText(batch, text, x, y);
        }
        font.getRenderOptions().color().reset();

        time += Jpize.getDeltaTime();
        if(input.isEnabled() && time * 2 % 2 < 1) {
            final float offsetX = font.getTextWidth(text.substring(0, input.getX()));
            batch.drawRect(x + offsetX, y, 3F, font.getHeightScaled(), 1F, 1F, 1F);
        }
        // scissor.remove("scissor");
        // scissor.apply();
    }

}
