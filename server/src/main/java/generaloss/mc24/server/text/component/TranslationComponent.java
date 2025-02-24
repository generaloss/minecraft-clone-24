package generaloss.mc24.server.text.component;

import generaloss.mc24.server.text.FormattedText;

public class TranslationComponent extends ITextComponent {

    private String[] key;
    private FormattedText[] arguments;

    public TranslationComponent(FormattedText textOf, String key, FormattedText... arguments) {
        super(textOf);
        this.set(key, arguments);
    }

    public String[] getKey() {
        return key;
    }

    public TranslationComponent setKey(String key) {
        this.key = key.split("%s");
        super.resetTextCache();
        return this;
    }

    public FormattedText[] getArguments() {
        return arguments;
    }

    public TranslationComponent setArguments(FormattedText... arguments) {
        this.arguments = arguments;
        super.resetTextCache();
        return this;
    }

    public TranslationComponent set(String key, FormattedText... arguments) {
        this.setKey(key);
        this.setArguments(arguments);
        return this;
    }


    public String solveText() {
        return null;
    }

}
