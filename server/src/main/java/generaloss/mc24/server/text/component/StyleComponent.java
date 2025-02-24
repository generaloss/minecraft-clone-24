package generaloss.mc24.server.text.component;

import generaloss.mc24.server.text.FormattedText;
import generaloss.mc24.server.text.formatting.TextFormatting;

public class StyleComponent extends IFormattingComponent {

    private int bits;

    public StyleComponent(FormattedText textOf) {
        super(textOf);
    }

    public StyleComponent(FormattedText textOf, StyleComponent style) {
        this(textOf);
        this.bits = style.bits;
    }

    public int getBits() {
        return bits;
    }

    public void set(StyleComponent style) {
        bits = style.bits;
    }

    public void reset() {
        bits = 0;
    }


    public void add(TextFormatting... formattings) {
        for(TextFormatting formatting: formattings)
            bits |= formatting.bit();
    }

    public void remove(TextFormatting formatting) {
        bits &= ~formatting.bit();
    }

    public boolean has(TextFormatting formatting) {
        return (bits & formatting.bit()) != 0;
    }

}
