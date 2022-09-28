package texteditor;
import java.util.Locale;

/**
 * Classname: APIClasses
 * Function: Provide all methods for API purpose
 * Author: Bharath Sukesh
 * Date: 27/10/21
 */
public class APIClasses
{
    public interface Plugin { // Implemented by EACH PLUGIN.
        void start(Control api, Locale locale);
        String exportLabel();
    }

    public interface Control { // The main application contains a class implementing this i.e. main app implements this
        void registerBtnPress(String label, DataCollector callback, InputReceiver inputCallback);
        void registerKeyPress(String keyName, DataCollector callback, InputReceiver inputCallback);
        void registerTextChanged(DataCollector callback);
        void addScriptName(String inName);
    }

    // Also implemented by plugins -- collects the actual data from the plugin/whatever data the plugin wants to return.
    public interface DataCollector
    {
        DataIntermed collect(DataIntermed dm);
    }

    public interface InputReceiver
    {
        boolean needInput();
    }
}