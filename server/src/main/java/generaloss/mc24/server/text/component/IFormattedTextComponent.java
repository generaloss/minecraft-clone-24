package generaloss.mc24.server.text.component;

import generaloss.mc24.server.text.FormattedText;

public abstract class IFormattedTextComponent {

    protected final FormattedText textOf;

    public IFormattedTextComponent(FormattedText textOf) {
        this.textOf = textOf;
    }

    public FormattedText getFormattedText() {
        return textOf;
    }

}
