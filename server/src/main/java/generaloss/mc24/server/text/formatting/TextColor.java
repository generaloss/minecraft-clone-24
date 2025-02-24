package generaloss.mc24.server.text.formatting;

import jpize.util.color.ImmutableColor;

public enum TextColor {

    WHITE        (1F   , 1F   , 1F   ),
    GRAY         (0.67F, 0.67F, 0.67F),
    DARK_GRAY    (0.33F, 0.33F, 0.33F),
    BLACK        (0F   , 0F   , 0F   ),
    RED          (1F   , 0.33F, 0.33F),
    DARK_RED     (0.78F, 0F   , 0F   ),
    GREEN        (0.33F, 1F   , 0.33F),
    DARK_GREEN   (0F   , 0.67F, 0F   ),
    BLUE         (0.12F, 0.56F, 1F   ),
    DARK_BLUE    (0F   , 0F   , 0.67F),
    AQUA         (0.33F, 1F   , 1F   ),
    DARK_AQUA    (0F   , 0.67F, 0.67F),
    PURPLE       (1F   , 0.33F, 1F   ),
    DARK_PURPLE  (0.67F, 0F   , 0.67F),
    YELLOW       (1F   , 1F   , 0.33F),
    ORANGE       (1F   , 0.67F, 0F   );

    private final ImmutableColor color;

    TextColor(float r, float g, float b) {
        this.color = new ImmutableColor(r, g, b);
    }

    public ImmutableColor color() {
        return color;
    }

}
