package texteditor;
/**
 * Classname: DataIntermed
 * Function: Provide an object/container for representing
 *          info needed by plugins and several other areas in the program.
 * Author: Bharath Sukesh
 * Date: 27/10/21
 */
public class DataIntermed
{
    private String text;
    private int caretPos;
    private String searchTerm;
    private int startIdx;
    private int endIdx;

    public DataIntermed(String text, int caretPos, String searchTerm) {
        this.text = text;
        this.caretPos = caretPos;
        this.searchTerm = searchTerm;
    }

    public DataIntermed(String text, int caretPos) {
        this.text = text;
        this.caretPos = caretPos;
    }


    public String getText() {return text;}

    public void setText(String text) {this.text = text;}

    public int getCaretPos() {return caretPos;}

    public void setCaretPos(int caretPos) {this.caretPos = caretPos;}

    public String getSearchTerm() {return searchTerm;}

    public void setSearchTerm(String searchTerm) {this.searchTerm = searchTerm;}

    public int getStartIdx() {return startIdx;}

    public void setStartIdx(int startIdx) {this.startIdx = startIdx;}

    public int getEndIdx() {return endIdx;}

    public void setEndIdx(int endIdx) {this.endIdx = endIdx;}
}
