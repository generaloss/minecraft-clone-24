package generaloss.mc24.server.text.component;

import generaloss.mc24.server.text.FormattedText;

public abstract class ITextComponent extends IFormattedTextComponent {

    private String textCache;

    public ITextComponent(FormattedText textOf) {
        super(textOf);
    }


    public String getTextCache() {
        return textCache;
    }

    public void resetTextCache() {
        textCache = null;
        super.textOf.resetTextCache();
    }

    public void cacheText() {
        if(textCache != null)
            return;

        if(this instanceof TextComponent text){
            textCache = text.getText();
        }else if(this instanceof TranslationComponent translation){
            // TODO: add languages for translation component
            textCache = "";
        }
    }

}
