import java.io.*;

/**
 * The logger is in charge of writing output to 'cron.log'. It does this in its own thread, but 
 * assumes that other threads will call the setMessage() in order to provide messages to log. (You 
 * need to fill in the details!)
 */
public class Logger
{
    private Thread logThread;
    private Object monitor = new Object();
    private String msg;
    
    public void logMessage(String newMessage) throws InterruptedException
    { // This method receives the user inputted message
        synchronized(monitor)
        {
            // Assign
            // Monitor.notify
            this.msg = newMessage;
            monitor.notifyAll();
            
        }
    }
    
    public void start()
    {
        Runnable myTask = () ->
        {
            try
            {
                while(true)
                {
                    synchronized(monitor)
                    {
                        while(msg == null)
                        {
                            monitor.wait();
                        }

                        try(PrintWriter writer = new PrintWriter(new FileWriter("cron.log",true)))
                        {
                            writer.println(msg);
                        }

                        msg = null;
                        //monitor.notifyAll();

                    } // End mutex
                    Thread.sleep(1000L);
                } // End while(true)
            }
            catch(InterruptedException e)
            {
                System.out.println("");
            }
            catch(IOException e)
            {

            }
        };
        logThread = new Thread(myTask, "task-thread");
        logThread.start();
        
        // ===================

    }
    
    public void stop()
    {
        if(logThread == null){throw new IllegalStateException(); }
        logThread.interrupt();
        logThread = null;
    }
}
