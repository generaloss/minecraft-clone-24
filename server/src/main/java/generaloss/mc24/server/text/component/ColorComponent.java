package generaloss.mc24.server.text.component;

import generaloss.mc24.server.text.FormattedText;
import jpize.util.color.AbstractColor;
import jpize.util.color.Color;

public class ColorComponent extends IFormattingComponent {

    private final Color color;

    public ColorComponent(FormattedText textOf) {
        super(textOf);
        this.color = new Color();
    }

    public ColorComponent(FormattedText textOf, AbstractColor color) {
        this(textOf);
        this.color.set(color);
    }

    public ColorComponent(FormattedText textOf, double red, double green, double blue, double alpha) {
        this(textOf);
        this.color.set(red, green, blue, alpha);
    }

    public ColorComponent(FormattedText textOf, double red, double green, double blue) {
        this(textOf);
        this.color.set(red, green, blue);
    }

    public Color color() {
        return color;
    }
}
