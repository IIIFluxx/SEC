import java.util.*;

/**
 * The scheduler keeps track of all the jobs, and runs each one at the appropriate time. (You need
 * to fill in the details!)
 */
public class Scheduler
{
    private List<Job> jobs;
    private Thread taskThread;
    private Object mutex;
    private int waitingTime;


     public Scheduler(){
         this.jobs = new ArrayList<Job>();
         this.mutex = new Object();
         this.waitingTime = 0;
     }

    public void addJob(Job newJob)
    {
        synchronized(mutex)
        {
            jobs.add(newJob);
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
                    // ... Do some work in the new thread.

                    // Run through the list of jobs

                    Job cur;
                    synchronized(mutex)
                    {
                        for(int ii=0;ii<jobs.size();ii++)
                        {
                            cur = jobs.get(ii);
                            if(cur != null)
                            {
                                if(cur.getDelay() != 0)
                                {
                                    if(waitingTime % cur.getDelay() == 0) // If true --> time to start a job.
                                    {
                                        // IF need to start right now, launch them in their own new threads.                                            
                                        Thread jobThread = new Thread();
                                        jobThread = new Thread(cur, "job-thread");
                                        jobThread.start();
                                    }
                                }                                
                            }
                        }
                        //System.out.println("WT: " + waitingTime);
                        /*• Repeats until an InterruptedException occurs. // Thrown in stop(). */  
                        
                    } // End mutex
                    Thread.sleep(1000L);
                    waitingTime++;
                } // End while(true)
            }
            catch(InterruptedException e)
            {
                
            }
        };
        taskThread = new Thread(myTask, "task-thread");
        taskThread.start();

    }

    public void stop() // When the user wants to exit, we need to shut down the other threads.
    {
        // * It needs to interrupt the scheduler thread. HOW? Call interrupt()
            // > Store a reference to the Thread object WHEN YOU CREATE IT.
        if(taskThread == null){throw new IllegalStateException(); }
        
        taskThread.interrupt();
        taskThread = null;
    }
}
