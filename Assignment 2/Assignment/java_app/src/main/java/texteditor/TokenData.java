package texteditor;
import java.util.ArrayList;
import java.util.List;
/**
 * Classname: TokenData
 * Function: Provide class for storing all Tokens and their respective information (from Keymap file).
 * Author: Bharath Sukesh
 * Date: 27/10/21
 */
public class TokenData
{
    private List<String> listOfKeys = new ArrayList<>();
    private String insdel; // Whether the string must be inserted or deleted.
    private String content; // Contents of the string itself to be inserted/deleted.
    private String location; // Where the string must be manipulated - start of line or at caret position.

    public TokenData(List<String> listOfKeys, String insdel, String content, String location) {
        this.listOfKeys = listOfKeys;
        this.insdel = insdel;
        this.content = content;
        this.location = location;
    }

    public List<String> getListOfKeys() {return listOfKeys;}

    public String getInsdel() {return insdel;}

    public String getContent() {return content;}

    public String getLocation() {return location;}

    public String toString()
    {   //toString == for e.g. '[ctrl, +, i]'.
        return removeBrackets(listOfKeys.toString()) + "," + getInsdel() + "," + getContent() + "," + getLocation();
    }

    public String getKeysOnly()
    {
        return removeBrackets(listOfKeys.toString());
    }

    // Removes brackets so that we can search for the exact key pressed (e.g. in string.equals() ).
    public String removeBrackets(String str)
    {
        String export = "";
        if(str.contains("[") && str.contains("]") && str.contains(" "))
        {
            String newStr = str.replace("[", "");
            export = newStr.replace("]", "");
            export = export.replace(" ", "");
        }
        return export;
    }

}
