package generaloss.mc24.client.renderer;

import generaloss.mc24.client.resources.ClientResources;
import generaloss.mc24.server.text.FormattedText;
import generaloss.mc24.server.text.component.*;
import generaloss.mc24.server.text.formatting.TextFormatting;
import jpize.util.color.Color;
import jpize.util.font.*;
import jpize.util.math.vector.Vec2f;
import jpize.util.mesh.TextureBatch;

public class FormattedTextRenderer {

    public static final float BOLD_SCALING = (9F / 8F);

    private final Font font;
    private final FontRenderOptions options;

    private final Color color;
    private boolean bold;
    private boolean underline;
    private boolean strikethrough;
    private boolean obfuscated;

    public FormattedTextRenderer() {
        this.color = new Color();

        this.font = ClientResources.FONTS.create("default", "fonts/default/font.fnt").resource();
        this.options = font.getRenderOptions();
        this.options.setInvLineWrap(true);
    }

    public FontRenderOptions options() {
        return options;
    }


    public void draw(TextureBatch batch, FormattedText formattedText, float x, float y) {
        color.reset();

        // cache text
        formattedText.cacheText();
        final String totalText = formattedText.getTextCache();

        // init
        batch.setTransformOrigin(0F, 0F);
        batch.rotate(options.getRotation());

        final Vec2f centerPos = font.getTextBounds(totalText).mul(options.rotationOrigin());
        centerPos.y *= options.getLineWrapSign();

        float cursorX = x;
        float cursorY = y;

        for(IFormattedTextComponent component: formattedText){
            if(component instanceof IFormattingComponent iformattingCopm) {
                // formatting
                if(iformattingCopm instanceof ColorComponent colorComp) {
                    color.set(colorComp.color());
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
                final String text = textComp.getTextCache();
                final Iterable<GlyphSprite> iterable = font.iterable(text);
                final GlyphIterator iterator = (GlyphIterator) iterable.iterator();

                // if(text.startsWith("\n"))
                //     cursorX = x;

                for(GlyphSprite sprite: iterable){
                    if(options.isCullLinesEnabled()) {
                        final float lineBottomY = (iterator.getCursorY() * options.scale().y + y);
                        final float lineTopY = (lineBottomY + font.getLineAdvanceScaled());
                        if(lineTopY < options.getCullLinesBottomY() || lineBottomY > options.getCullLinesTopY()){
                            iterator.skipLine();
                            continue;
                        }
                    }

                    if((char) sprite.getCode() == ' ' || !sprite.isRenderable())
                        continue;

                    final Vec2f renderPos = new Vec2f(sprite.getX(), sprite.getY());
                    renderPos.y -= font.getDescentScaled();
                    renderPos
                        .sub(centerPos)
                        .rotate(options.getRotation())
                        .add(centerPos)
                        .add(cursorX, cursorY);
                    renderPos.y += font.getDescentScaled();

                    batch.draw(sprite.getPage(), sprite.getRegion(), renderPos.x, renderPos.y, sprite.getWidth(), sprite.getHeight(), color);
                    if(bold){
                        final float boldX = (renderPos.x + options.scale().x);
                        batch.draw(sprite.getPage(), sprite.getRegion(), boldX, renderPos.y, sprite.getWidth(), sprite.getHeight(), color);
                        //iterator.advance(options.scale().x, 0F);
                    }
                }

                cursorX += iterator.getCursorX() * options.scale().x;
                // if(text.endsWith("\n"))
                //     cursorX = x;

                final float yCorrection = 0;//(options.isInvLineWrap() ? font.getLineAdvance() * options.advanceFactor().y : 0);
                cursorY += (iterator.getCursorY() + yCorrection) * options.scale().y;
            }
        }
    }

}
