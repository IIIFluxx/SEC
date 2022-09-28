package edu.curtin.comp3003.filesearcher;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.*;

import javax.swing.SwingUtilities;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FSFilter 
{
    private String searchDir;
    private String searchStr;
    private FSUserInterface ui;

    private Thread searchThread, filterThread;
    private static final String POISON = "POISON";
    private ExecutorService es;

    public FSFilter(String inSearchDir, String inSearchStr, FSUserInterface inUI) 
    {
        this.searchDir = inSearchDir;
        this.searchStr = inSearchStr;
        this.ui = inUI;
        /*
        To do this , have the FILTER thread work with an ExecutorService object and
        perform each individual file check in its own separate thread.
        */
        // STEP 1: Introduce two new threads.
        Runnable searchTask = () ->
        {
            search();
        };
        es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        searchThread = new Thread(searchTask, "search-thread");
        searchThread.start();





        System.out.println("Added to UI");

    }


    // STEP 2: Search thread should enqueue/put() each file it encounters.
    public void search() 
    {
        try
        {
            // Recurse through the directory tree
            Files.walkFileTree(Paths.get(searchDir), new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                {
                    // [Processing]
                    String fileStr = file.toString();
                    try
                    {
                        System.out.println("Added " + fileStr);
                        //queue.put(fileStr);
                        es.submit(() ->
                        {
                            filter(fileStr);
                        });
                    }
                    finally
                    {
                        if(fileStr == null || fileStr.isEmpty())
                        {
                            //queue.put(POISON);
                            System.out.println("Added POISON");
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch(IOException e)
        {
            ui.showError(e.getClass().getName() + ": " + e.getMessage());
        }
    }


    // STEP 3: Filter thread should dequeue each queue element, add to LL if search term matches element.
    private void filter(String fileStr)
    {
        //while(true)
        //{
            String result;
            //result = queue.take();
            result = fileStr;
            //if(result.contains(searchStr))
            //{
            System.out.println("Filename = " + result);

            File file = new File(result);
            try
            {
                // Basic File IO based off OOPD.
                Scanner sc = new Scanner(file);
                int lNum = 0;
                while(sc.hasNextLine())
                {
                    String line = sc.nextLine();
                    if(line.contains(searchStr))
                    {
                        SwingUtilities.invokeLater(() ->
                        {
                            ui.addResult(result);
                        });
                        break;
                    }
                }
            }
            catch(IOException ioEx)
            {

            }
        //}
        //}
    }
}