package generaloss.mc24.server.text.formatting;

public enum TextFormatting {

    ITALIC,
    BOLD,
    UNDERLINE,
    STRIKETHROUGH,
    OBFUSCATED;

    private final int bit;

    TextFormatting() {
        this.bit = (1 << this.ordinal());
    }

    public int bit() {
        return bit;
    }

}
