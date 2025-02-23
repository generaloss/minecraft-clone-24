package generaloss.mc24.server.text;

import generaloss.mc24.server.text.component.FormattedTextComponent;
import generaloss.mc24.server.text.style.TextStyle;
import jpize.util.color.Color;

import java.util.ArrayList;
import java.util.List;

public class FormattedText {

    private final TextStyle nextStyle;
    private final Color nextColor;
    private final List<FormattedTextComponent> components;

    public FormattedText() {
        this.nextStyle = new TextStyle();
        this.nextColor = new Color();
        this.components = new ArrayList<>();
    }

    public void

}
