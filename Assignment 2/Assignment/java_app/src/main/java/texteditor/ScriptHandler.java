package texteditor;
import org.python.core.*;
import org.python.util.*;
/**
 * Classname: ScriptHandler
 * Function: Provides methods for executing Python scripts.
 * Author: Bharath Sukesh
 * Date: 27/10/21
 */
// Invoking a Python script using Jython.
public class ScriptHandler
{
    public void runScript(APIClasses.Control api, String pythonScript)
    {
        // Initialise interpreter
        PythonInterpreter interpreter = new PythonInterpreter();
        // Bind API to Script environment
        // Choose Java objects (api) to appear as global variables from within the Python code.
        // Enables the script to access the API.
        interpreter.set("api",api);
        // Run the python script.
        try
        {
            interpreter.exec(pythonScript);
        }
        catch(PySyntaxError e)
        {
            System.out.println("Script chosen was not valid! Jython error occurred");
            System.out.println(e.getMessage());
        }
    }
}