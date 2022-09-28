package texteditor;

import java.util.Locale;
import java.util.ResourceBundle;
/**
 * Classname: DatePlugin
 * Function: Provide implementation for DatePlugin.
 *          Implements all methods in Plugin interface
 * Author: Bharath Sukesh
 * Date: 27/10/21
 */
public class DatePlugin implements APIClasses.Plugin
{
    private Locale locale;
    private ResourceBundle bundle;

    /* The plugin init. itself. */
    @Override
    public void start(APIClasses.Control api, Locale locale) {
        // register callbacks.
        System.out.println("---DatePlugin.start()---");
        bundle = ResourceBundle.getBundle("datebundle", locale);
        api.registerBtnPress(bundle.getString("date_txt"), new DateDataSource(locale), null); // Null because no input needed.
        this.locale = locale;
    }

    public String exportLabel()
    {
        bundle = ResourceBundle.getBundle("datebundle", locale);
        return bundle.getString("date_txt");
    }

}

