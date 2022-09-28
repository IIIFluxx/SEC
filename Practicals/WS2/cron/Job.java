import java.io.*;

/**
 * Represents a job (command) to be run, and performs the execution. 
 * ==========================================================================================
 * Represents a particular job/command to be run at periodic intervals. 
 * Each command must be run in its own separate thread, in order to progressively capture its output. 
 */
public class Job implements Runnable
{
    /*
    From UML: 
    - command: string
    - delay: int
    - logger: Logger
    + run() */

    private String command;
    private int delay;
    private Logger logger;
    
    public Job(String command2, int delay2, Logger logger2) 
    {
        command = command2;
        delay = delay2;
        logger = logger2;
    }

    // REF: Method populated from Worksheet given code.
    // PURPOSE: The actual execution of external commands in Java.
    @Override
    public void run() 
    {
        // Assume 'command' is a string containing the command the
        // execute. Then we initially run it as follows:
        Process proc;
        try 
        {
            proc = Runtime.getRuntime().exec(command);

            // Arrange to capture the command's output, line by line.
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
            new InputStreamReader(proc.getInputStream()));
            String line = reader.readLine();
            while(line != null)
            {
            output.append(line);
            output.append('\n');
            line = reader.readLine();
            }
            // We've now reached the end of the command's output, which
            // generally means the command has finished.

            logger.logMessage(command + ": " + output.toString());
        } catch (IOException e) { e.printStackTrace(); } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getDelay() {
        return delay;
    }

    public String getCommand() {
        return command;
    }
    
}
