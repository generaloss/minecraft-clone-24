package generaloss.mc24.client.renderer;

import generaloss.mc24.client.resources.ClientResources;
import generaloss.mc24.server.text.FormattedText;
import generaloss.mc24.server.text.component.*;
import generaloss.mc24.server.text.formatting.TextFormatting;
import jpize.util.color.Color;
import jpize.util.font.*;
import jpize.util.math.Maths;
import jpize.util.math.vector.Vec2f;
import jpize.util.mesh.TextureBatch;

public class FormattedTextRenderer {

    public static final float BOLD_SCALING = (9F / 8F);
    public static final float SHADOW_FACTOR = 0.25F;

    private final Font font;
    private final FontRenderOptions options;

    private final Color color;
    private final Color shadowColor;
    private boolean bold;
    private boolean underline;
    private boolean strikethrough;
    private boolean obfuscated;

    public FormattedTextRenderer() {
        this.color = new Color();
        this.shadowColor = new Color().mulRGB(SHADOW_FACTOR);

        this.font = ClientResources.FONTS.create("default", "fonts/default/font.fnt").resource();
        this.options = font.getOptions();
        this.options.setInvLineWrap(true);
    }

    public FontRenderOptions options() {
        return options;
    }


    public void draw(TextureBatch batch, FormattedText formattedText, float x, float y) {
        // cache text
        final String totalText = formattedText.getCachedText();

        // init
        batch.setTransformOrigin(0F, 0F);
        batch.rotate(options.getRotation());

        final Vec2f centerPos = font.getTextBounds(totalText).mul(options.rotationOrigin());
        centerPos.y *= options.getLineWrapSign();

        float cursorX = x;
        float cursorY = y;

        final Vec2f scale = options.scale();

        final float lineHeight = (font.getHeight() / 8F);
        final float lineHeightScaled = (lineHeight * scale.y);
        this.options.setNewLineGap(lineHeight);
        this.options.setLineGap(lineHeight);

        for(IFormattedTextComponent component: formattedText){
            if(component instanceof IFormattingComponent iformattingCopm) {
                // formatting
                if(iformattingCopm instanceof ColorComponent colorComp) {
                    color.set(colorComp.color());
                    shadowColor.set(color).mulRGB(SHADOW_FACTOR);

                }else if(iformattingCopm instanceof StyleComponent styleComp) {
                    final boolean italic = styleComp.has(TextFormatting.ITALIC);
                    batch.shear(italic ? 15F : 0F, 0F);

                    bold          = styleComp.has(TextFormatting.BOLD);
                    underline     = styleComp.has(TextFormatting.UNDERLINE);
                    strikethrough = styleComp.has(TextFormatting.STRIKETHROUGH);
                    obfuscated    = styleComp.has(TextFormatting.OBFUSCATED);
                }

            }else if(component instanceof ITextComponent textComp){
                // text
                String text = textComp.getTextCache();
                if(obfuscated)
                    text = buildObfuscated(text);

                final GlyphIterable iterable = font.iterable(text);
                final GlyphIterator iterator = iterable.iterator();

                for(GlyphSprite sprite: iterable){
                    if(options.isCullLinesEnabled()) {
                        final float lineBottomY = (iterator.position().y * scale.y + y);
                        final float lineTopY = (lineBottomY + font.getLineAdvanceScaled());
                        if(lineTopY < options.getCullLinesBottomY() || lineBottomY > options.getCullLinesTopY()){
                            iterator.hideLine();
                            continue;
                        }
                    }

                    if(iterator.character() == ' ')
                        continue;

                    final Vec2f renderPos = new Vec2f(sprite.getX(), sprite.getY());
                    renderPos.y -= font.getDescentScaled();
                    renderPos
                        .sub(centerPos)
                        .rotate(options.getRotation())
                        .add(centerPos)
                        .add(cursorX, cursorY);
                    renderPos.y += font.getDescentScaled();

                    // shadow
                    batch.draw(sprite.getPage(), sprite.getRegion(), renderPos.x + lineHeightScaled, renderPos.y - lineHeightScaled, sprite.getWidth(), sprite.getHeight(), shadowColor);
                    if(bold){
                        // bold shadow
                        final float boldX = (renderPos.x + scale.x);
                        batch.draw(sprite.getPage(), sprite.getRegion(), boldX + lineHeightScaled, renderPos.y - lineHeightScaled, sprite.getWidth(), sprite.getHeight(), shadowColor);
                    }
                    // normal
                    batch.draw(sprite.getPage(), sprite.getRegion(), renderPos.x, renderPos.y, sprite.getWidth(), sprite.getHeight(), color);
                    // formatting
                    if(bold) {
                        final float boldX = (renderPos.x + scale.x);
                        batch.draw(sprite.getPage(), sprite.getRegion(), boldX, renderPos.y, sprite.getWidth(), sprite.getHeight(), color);
                        iterator.cursor().x += (scale.x / 8F);
                    }
                    if(iterator.nextCursorX() == 0F) {
                        if(!underline && !strikethrough)
                            continue;

                        final float lineWidth = (iterator.cursor().x + iterator.nextAdvanceX()) * scale.x;

                        if(underline){
                            final float lineY = (cursorY + iterator.position().y * scale.y - lineHeightScaled);
                            batch.drawRect(cursorX, lineY, lineWidth, lineHeight * scale.y, color);
                        }
                        if(strikethrough){
                            final float lineY = (cursorY + iterator.position().y * scale.y + font.getHeightScaled() / 8F * 3F);
                            batch.drawRect(cursorX, lineY, lineWidth, lineHeight * scale.y, color);
                        }
                    }
                }

                cursorX += (iterator.nextCursorX() * scale.x);
                if(iterator.nextCursorX() == 0F)
                    cursorX = x;

                cursorY += (iterator.nextCursorY() * options.getLineWrapSign() * options.scale().y);
            }
        }

        // reset style
        batch.shear(0F, 0F); // italic
        bold          = false;
        underline     = false;
        strikethrough = false;
        obfuscated    = false;
        // reset color
        color.reset();
        shadowColor.reset().mulRGB(SHADOW_FACTOR);
    }

    public static String buildObfuscated(String text) {
        final StringBuilder result = new StringBuilder();
        for(int i = 0; i < text.length(); i++){
            char c = text.charAt(Maths.random(text.length() - 1));
            result.append(c);
        }
        return result.toString();
    }

}
