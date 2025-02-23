package generaloss.mc24.server.text.component;

public class TranslationComponent extends FormattedTextComponent {

    private String[] key;
    private FormattedTextComponent[] arguments;

    public TranslationComponent(String key, FormattedTextComponent... arguments) {
        this.set(key, arguments);
    }


    public String[] getKey() {
        return key;
    }

    public TranslationComponent setKey(String key) {
        this.key = key.split("%s");
        return this;
    }


    public FormattedTextComponent[] getArguments() {
        return arguments;
    }

    public TranslationComponent setArguments(FormattedTextComponent... arguments) {
        this.arguments = arguments;
        return this;
    }


    public TranslationComponent set(String key, FormattedTextComponent... arguments) {
        this.setKey(key);
        this.setArguments(arguments);
        return this;
    }


    public String solveText() {
        return null;
    }

}
