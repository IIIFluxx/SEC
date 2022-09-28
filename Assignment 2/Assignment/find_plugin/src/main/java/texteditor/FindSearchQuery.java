package texteditor;
/**
 * Classname: FindSearchQuery
 * Function: Implements all methods for InputReceiver interface.
 * Author: Bharath Sukesh
 * Date: 27/10/21
 */
public class FindSearchQuery implements APIClasses.InputReceiver
{
    @Override
    public boolean needInput() {
        return true;
    }
}
