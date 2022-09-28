package texteditor;

import java.lang.reflect.*;
import java.util.Locale;
/**
 * Classname: pluginController
 * Function: Provides methods for firing off to Plugin methods, given the classname and locale from UIExample.
 * Author: Bharath Sukesh
 * Date: 27/10/21
 */
public class pluginController
{

    public String addPlugin(String className, UIExample ui, Locale locale)
    {
        // Finds start() method via reflection - return boolean in Plugin Handler indicating success.
        // If so ---> add to list in UI.
        Class<?> c = null;
        String export = "Not found";
        try
        {
            c = Class.forName(className);
            Constructor con = c.getConstructor(new Class[]{});
            Object obj = (Object)con.newInstance();
            // Make an instance of an internal class that implements Control.
            Method m = c.getMethod("start", new Class[]{APIClasses.Control.class, Locale.class});
            m.invoke(obj, ui, locale); //  Need UI Class to access it in python script (api calls).
            Method m2 = c.getMethod("exportLabel");
            Object returnValue = m2.invoke(obj);
            export = returnValue.toString();

        } catch (ClassNotFoundException | NoSuchMethodException e)
        {
            System.out.println("Reflection error!");
            System.out.println("Method or Class was not found.");
            System.out.println(e.getMessage());
        } catch (ReflectiveOperationException e) {
            System.out.println("Reflection error!");
            System.out.println(e.getMessage());
        }
        return export;
    }
}
