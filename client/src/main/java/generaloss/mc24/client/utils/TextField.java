package generaloss.mc24.client.utils;

import jpize.context.Jpize;
import jpize.context.input.MouseBtn;
import jpize.util.font.Font;
import jpize.util.input.TextInput;
import jpize.util.math.geometry.Intersector;
import jpize.util.mesh.TextureBatch;

// temporary class
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
        this.input = new TextInput().setMaxLines(1).setTabSpaces(0);
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


    public boolean isFocused() {
        return input.isEnabled();
    }

    public void setFocused(boolean focused) {
        input.enable(focused);
    }


    public void render(TextureBatch batch) {
        final float height = font.getLineAdvanceScaled();
        final float width = font.getTextWidth("W".repeat(30));

        batch.drawRectBlack(x, Jpize.getHeight() - y, width, height, 0.5F);

        if(MouseBtn.pressedAny(MouseBtn.values())){
            this.setFocused(Intersector.isPointOnRect(Jpize.getX(), Jpize.getY(), x, Jpize.getHeight() - y, width, height));
            time = 0;
        }

        // scissor.put("scissor", x, y, width, height);
        // scissor.apply();

        final String text = input.makeString();
        if(text.isEmpty()) {
            font.getOptions().color().set(0.7F);
            font.drawText(batch, hint, x, Jpize.getHeight() - y + height);
        }else{
            font.drawText(batch, text, x, Jpize.getHeight() - y + height);
        }
        font.getOptions().color().reset();

        time += Jpize.getDeltaTime();
        if(this.isFocused() && time * 2 % 2 < 1) {
            final float offsetX = font.getTextWidth(text.substring(0, input.getX()));
            batch.drawRect(x + offsetX, Jpize.getHeight() - y, 3F, font.getHeightScaled(), 1F, 1F, 1F);
        }
        // scissor.remove("scissor");
        // scissor.apply();
    }

}
