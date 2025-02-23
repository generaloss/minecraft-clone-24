package generaloss.mc24.server.text.component;

public class TextComponent extends FormattedTextComponent {

    private String text;

    public TextComponent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public TextComponent setText(String text) {
        this.text = text;
        return this;
    }

    public TextComponent appendText(String text) {
        this.text += text;
        return this;
    }

}
