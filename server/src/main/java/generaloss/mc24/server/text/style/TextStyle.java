package generaloss.mc24.server.text.style;

public class TextStyle {

    private int bits;

    public TextStyle() { }

    public TextStyle(TextStyle style) {
        this.setStyle(style);
    }


    public int getBits() {
        return bits;
    }

    public void setStyle(TextStyle style) {
        bits = style.bits;
    }


    public void reset() {
        bits = 0;
    }

    public void add(TextFormatting formatting) {
        bits |= formatting.bit();
    }

    public void remove(TextFormatting formatting) {
        bits &= ~formatting.bit();
    }

    public boolean has(TextFormatting formatting) {
        return (bits & formatting.bit()) != 0;
    }

}
