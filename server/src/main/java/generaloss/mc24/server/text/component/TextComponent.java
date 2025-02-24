package generaloss.mc24.server.text.component;

import generaloss.mc24.server.text.FormattedText;

public class TextComponent extends ITextComponent {

    private String text;

    public TextComponent(FormattedText textOf, String text) {
        super(textOf);
        this.text = text;
    }


    public String getText() {
        return text;
    }

    public TextComponent setText(String text) {
        this.text = text;
        super.resetTextCache();
        return this;
    }

    public TextComponent appendText(String text) {
        this.text += text;
        super.resetTextCache();
        return this;
    }

}
