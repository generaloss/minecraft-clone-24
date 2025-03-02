package generaloss.mc24.server.text;

import generaloss.mc24.server.text.component.*;
import generaloss.mc24.server.text.formatting.TextColor;
import generaloss.mc24.server.text.formatting.TextFormatting;
import jpize.util.color.AbstractColor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FormattedText implements Iterable<IFormattedTextComponent> {

    private final List<IFormattedTextComponent> components;
    private String textCache;

    public FormattedText() {
        this.components = new ArrayList<>();
    }


    public FormattedText add(IFormattedTextComponent component) {
        this.components.add(component);
        return this;
    }

    public FormattedText add(int index, IFormattedTextComponent component) {
        this.components.add(index, component);
        return this;
    }

    public FormattedText set(int index, IFormattedTextComponent component) {
        this.components.set(index, component);
        return this;
    }

    public FormattedText remove(IFormattedTextComponent component) {
        this.components.remove(component);
        return this;
    }

    public FormattedText remove(int index) {
        this.components.remove(index);
        return this;
    }


    public String getTextCache() {
        return textCache;
    }

    public void resetTextCache() {
        textCache = null;
    }

    public String getCachedText() {
        if(textCache != null)
            return textCache;

        final StringBuilder textBuilder = new StringBuilder();
        for(IFormattedTextComponent component: components){
            if(component instanceof ITextComponent itext){
                itext.cacheText();
                textBuilder.append(itext.getTextCache());
            }
        }
        textCache = textBuilder.toString();
        return textCache;
    }


    @Override
    public Iterator<IFormattedTextComponent> iterator() {
        return components.iterator();
    }


    public FormattedText color() { // clear color
        return this.add(new ColorComponent(this));
    }

    public FormattedText color(AbstractColor color) {
        return this.add(new ColorComponent(this, color));
    }

    public FormattedText color(TextColor color) {
        return this.color(color.color());
    }

    public FormattedText color(double red, double green, double blue, double alpha) {
        return this.add(new ColorComponent(this, red, green, blue, alpha));
    }

    public FormattedText color(double red, double green, double blue) {
        return this.add(new ColorComponent(this, red, green, blue));
    }

    public FormattedText colorRGB(int color) {
        final ColorComponent component = new ColorComponent(this);
        component.color().setRGB(color);
        return this.add(component);
    }

    public FormattedText colorRGBA(int color) {
        final ColorComponent component = new ColorComponent(this);
        component.color().setRGBA(color);
        return this.add(component);
    }


    public FormattedText style() { // clear formatting
        if(components.get(components.size() - 1) instanceof StyleComponent component) {
            component.reset();
            return this;
        }
        return this.add(new StyleComponent(this));
    }

    public FormattedText style(TextFormatting... formattings) {
        if(components.get(components.size() - 1) instanceof StyleComponent component) {
            component.add(formattings);
            return this;
        }
        final StyleComponent component = new StyleComponent(this);
        component.add(formattings);
        return this.add(component);
    }


    public FormattedText text(String text) {
        return this.add(new TextComponent(this, text));
    }


    public FormattedText translation(String key, FormattedText... arguments) {
        return this.add(new TranslationComponent(this, key, arguments));
    }

}
