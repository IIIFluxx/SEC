package texteditor;

import java.util.Locale;
import java.util.ResourceBundle;
/**
 * Classname: FindPlugin
 * Function: Provide implementation for FindPlugin.
 *          Implements all methods in Plugin interface
 * Author: Bharath Sukesh
 * Date: 27/10/21
 */
public class FindPlugin implements APIClasses.Plugin
{
    private Locale locale;
    private ResourceBundle bundle;

    @Override
    public void start(APIClasses.Control api, Locale locale) {
        // register callbacks.
        System.out.println("---FindPlugin.start() run ---");
        bundle = ResourceBundle.getBundle("findbundle", locale);
        api.registerBtnPress(bundle.getString("find_txt"), new FindDataSource(locale), new FindSearchQuery());
        api.registerKeyPress("F3", new FindDataSource(locale), new FindSearchQuery());
        this.locale = locale;
    }

    public String exportLabel()
    {
        bundle = ResourceBundle.getBundle("findbundle", locale);
        return bundle.getString("find_txt");
    }
}



