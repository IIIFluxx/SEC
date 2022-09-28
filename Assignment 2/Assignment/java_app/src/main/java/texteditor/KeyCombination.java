package texteditor;
/**
 * Classname: KeyCombination
 * Function: Class for storing key combinations parsed from keymap file.
 * Author: Bharath Sukesh
 * Date: 27/10/21
 */
public class KeyCombination
{
    private char charac;
    private boolean control;
    private boolean shift;
    private boolean alt;
    private TokenData tokens;

    public KeyCombination(char charac, boolean control, boolean shift, boolean alt, TokenData tokens) {
        this.charac = charac;
        this.control = control;
        this.shift = shift;
        this.alt = alt;
        this.tokens = tokens;
    }

    public char getCharac() {return charac;}

    public boolean getControl() {return control;}

    public boolean getShift() {return shift;}

    public boolean getAlt() {return alt;}

    public TokenData getTokens() {return tokens;}
}
