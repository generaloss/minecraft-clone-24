package generaloss.mc24.server.text.component;

import generaloss.mc24.server.text.style.TextColor;
import generaloss.mc24.server.text.style.TextFormatting;
import generaloss.mc24.server.text.style.TextStyle;
import jpize.util.color.AbstractColor;
import jpize.util.color.Color;

public abstract class FormattedTextComponent {

    private final Color color;
    private final TextStyle style;
    private String textCache;

    public FormattedTextComponent() {
        this.color = new Color();
        this.style = new TextStyle();
    }


    public Color color() {
        return color;
    }

    public TextStyle style() {
        return style;
    }


    public FormattedTextComponent color(AbstractColor color) {
        this.color.set(color);
        return this;
    }

    public FormattedTextComponent color(TextColor color) {
        return this.color(color.color());
    }

    public FormattedTextComponent color(int color) {
        this.color.setRGB(color);
        return this;
    }


    public FormattedTextComponent formatting(TextFormatting formatting) {
        style.add(formatting);
        return this;
    }


    public String getTextCache() {
        return textCache;
    }

    public void setTextCache(String textCache) {
        this.textCache = textCache;
    }

}
