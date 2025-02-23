package generaloss.mc24.client.renderer;

import generaloss.mc24.server.text.component.FormattedTextComponent;
import generaloss.mc24.server.text.component.TextComponent;
import generaloss.mc24.server.text.component.TranslationComponent;
import jpize.util.font.Font;
import jpize.util.font.FontRenderOptions;
import jpize.util.math.vector.Vec2f;
import jpize.util.mesh.TextureBatch;

import java.util.ArrayList;
import java.util.List;

public class ComponentRenderer {

    private final List<FormattedTextComponent> drawComponents;
    private String drawText;

    public ComponentRenderer() {
        this.drawComponents = new ArrayList<>();
        this.drawText = "";
    }

    private void setupDrawComponents(FormattedTextComponent component) {
        drawComponents.add(component);
        this.appendDrawText(component);

        for(FormattedTextComponent child: component.children())
            this.setupDrawComponents(child);
    }

    private void appendDrawText(FormattedTextComponent component) {
        if(component instanceof TextComponent text) {
            component.setTextCache(text.getText());
        }else if(component instanceof TranslationComponent translation) {
            component.setTextCache(translation.get());
        }

        drawText += component.getTextCache();
    }


    public void draw(Font font, TextureBatch batch, FormattedTextComponent component, float x, float y) {
        this.setupDrawComponents(component);

        final StringBuilder text = new StringBuilder();
        for(FormattedTextComponent comp: drawComponents){

        }

        // init
        final FontRenderOptions options = font.getRenderOptions();
        batch.setTransformOrigin(0F, 0F);
        batch.rotate(options.getRotation());
        batch.shear(font.isItalic() ? options.getItalicAngle() : 0F, 0F);

        final Vec2f centerPos = font.getTextBounds(text).mul(options.rotationOrigin());
        centerPos.y *= options.getLineWrapSign();

        int advanceX = 0;
        int advanceY = 0;

        for(FormattedTextComponent comp: drawComponents){

        }

        drawComponents.clear();
        drawText = "";
    }

}
