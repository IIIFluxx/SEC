package edu.curtin.comp3003.filesearcher;

import javafx.application.Platform;

import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.*;

public class SearchController
{
    private LinkedList<String> list;

    private String searchDir;
    private FSUserInterface ui;
    private BlockingQueue<ComparisonResult> queue;

    private Thread searchThread, writeThread;
    private ExecutorService es;
    private static final String POISON = "POISON";
    private int compareTally;
    private Object mutex;
    public SearchController(String inSearchDir, FSUserInterface inUI)
    {
        this.searchDir = inSearchDir;
        this.ui = inUI;
        this.queue = new LinkedBlockingQueue<ComparisonResult>();
        this.list = new LinkedList<String>();
        this.compareTally = 0;
        this.mutex = new Object();

        // STEP 1: Introduce new threads.
        Runnable searchTask = () ->
        {
            search();
        };

        // Thread pool
        es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        searchThread = new Thread(searchTask, "search-thread");

        searchThread.start();

    }



    // Search thread should enqueue/put() each file it encounters.
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
                        if(new File(fileStr).length() > 0)
                        {
                            // .txt .md .java .cs
                            if(fileStr.endsWith("txt") || fileStr.endsWith("md")
                                    || fileStr.endsWith("java") || fileStr.endsWith("cs"))
                            {
                                //System.out.println("Added " + fileStr);
                                list.add(fileStr);
                            }
                        }
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

            for(int ii=0;ii<list.size();ii++)
            {
                String curFile = list.get(ii);
                es.submit(() ->
                {
                    filter(curFile); //NOTE: Thread Pool use #1.
                });
            }
            toFile();
        }
        catch(IOException e)
        {
            Platform.runLater(() ->
            {
                e.printStackTrace();
                ui.showError("IO ERROR: " + e.getClass().getName() + ": " + e.getMessage());
            });
        }
    }

    private void filter(String fileStr)
    {
        String result;

        result = fileStr;

        //System.out.println("Filename = " + result);

        try
        {
            // Add a new eligible file to the queue.

            int index = 0;
            // ==================================================== LIST ==========================================
            for(int ii=0;ii<list.size();ii++)
            {
                if(list.get(ii) == result)
                {
                    index = ii;
                }
            }

            for(int ii=index;ii>=0;--ii)
            {
                String curFile = list.get(ii);
                if(curFile != result)
                {
                    String f1Str = Files.readString(Path.of(result));
                    String f2Str = Files.readString(Path.of(curFile));
                    //System.out.println("f1:" + f1Str); // Works
                    //System.out.println("f2:" + f2Str); // Works.
                    char[] c1 = f1Str.toCharArray();
                    char[] c2 = f2Str.toCharArray();

                    es.submit(() ->
                    {
                        ComparisonResult newRes = new ComparisonResult(result, curFile, calcSimilarity(c1, c2));

                        synchronized(mutex)
                        {
                            compareTally++;
                            //System.out.println("Tally currently = " + compareTally);
                            //ui.updateProgBar(comp done/calc total comparisons());
                            Platform.runLater(() ->
                            {
                                ui.updateProgBar(compareTally/calcTotComp());
                            });
                        }

                        try // For File Writing.
                        {
                            queue.put(newRes); // Intended to throw INT EXCEP because no longer running. Caught below.
                        }
                        catch (InterruptedException e)
                        {
                            Platform.runLater(() ->
                            {
                                e.printStackTrace();
                            });
                        }

                        // Thread that writes to file; waits until queue filled, then writes to file.

                        if(newRes.getSimilarity() > 0.5)
                        {
                            //System.out.println("To GUI = " + result);
                            Platform.runLater(() ->
                            {
                                ui.addResult(newRes);
                            });
                        }
                        //System.out.println("Reached");
                    });
                }
            }

        } catch (FileNotFoundException e) {
            Platform.runLater(() ->
            {
                e.printStackTrace();
                ui.showError("FILE ERROR: " + e.getClass().getName() + ": " + e.getMessage());
            });
        } catch (IOException e) {
            Platform.runLater(() ->
            {
                e.printStackTrace();
                ui.showError("IO ERROR: " + e.getClass().getName() + ": " + e.getMessage());
            });
        }
    }

    public void stop() // When the user wants to exit, we need to shut down the other threads.
    {
        // * It needs to interrupt the scheduler thread. HOW? Call interrupt()
        // > Store a reference to the Thread object WHEN YOU CREATE IT.

        if(writeThread == null){throw new IllegalStateException(); }
        if(searchThread == null){throw new IllegalStateException(); }
        writeThread.interrupt();
        writeThread = null;
        searchThread.interrupt();
        searchThread = null;
        es.shutdownNow();
    }

    private double calcTotComp() {
        // This function contains the formula from the Assigment Specification
        // c = 0.5 * (f*2 - f)
        // c = number of comparisons.
        // f2 = # of files squared
        // f = # of files.
        double ans = 0;
        ans = (0.5 * ((list.size()*list.size()) - list.size()));
        return ans;
    }

    public void toFile()
    {
        Runnable myTask = () ->
        {
            try
            {
                while(true)
                {
                    ComparisonResult result;

                    result = queue.take(); // Intended to throw INT EXCEP because no longer running. Caught below.

                    try(PrintWriter writer = new PrintWriter(new FileWriter("results.csv",true)))
                    {
                        //System.out.println("Writing to file... ");
                        // First two columns - filenames, third is similarity
                        writer.println(result.getFile1() + "," + result.getFile2() + "," + result.getSimilarity());
                    }
                }
            }
            catch(IOException ioEx)
            {
                Platform.runLater(() ->
                {
                    ioEx.printStackTrace();
                    ui.showError("IO ERROR: " + ioEx.getClass().getName() + ": " + ioEx.getMessage());
                });
            }
            catch(InterruptedException iEx3)
            {
                Platform.runLater(() ->
                {
                    iEx3.printStackTrace();
                });
            }
        };

        writeThread = new Thread(myTask, "write-thread");
        writeThread.start();

    }

    public double calcSimilarity(char[] file1, char[] file2)
    {
        int[][] subsolutions = new int[file1.length + 1][file2.length + 1];
        boolean[][] directionsLeft = new boolean[file1.length + 1][file2.length + 1];
        int matches;
        // The table rows represent characters in the first string, and columns represent
        // characters in the second.

        for(int ii = 0; ii <= file1.length; ii++)
        {
            subsolutions[ii][0] = 0;
        }

        for(int jj = 0; jj <= file2.length; jj++)
        {
            subsolutions[0][jj] = 0;
        }

        for(int ii = 1; ii <= file1.length; ii++) {
            for (int jj = 1; jj <= file2.length; jj++) {
                //System.out.println(subsolutions[ii][jj]);
                    if(file1[ii-1] == file2[jj-1])
                    {
                        subsolutions[ii][jj] = subsolutions[ii-1][jj-1] + 1;
                    }
                    else if(subsolutions[ii - 1][jj] > subsolutions[ii][jj - 1])
                    {
                        subsolutions[ii][jj] = subsolutions[ii - 1][jj];
                        directionsLeft[ii][jj] = true;
                    }
                    else
                    {
                        subsolutions[ii][jj] = subsolutions[ii][jj - 1];
                        directionsLeft[ii][jj] = false;
                    } // End if
            } // End for
        } // End for

        matches = 0;
        int ii, jj;
        ii = file1.length;
        jj = file2.length;

        while(ii>0 && jj>0)
        {
            if(file1[ii - 1] == file2[jj - 1])
            {
                matches += 1;
                ii -= 1;
                jj -= 1;
            }
            else if(directionsLeft[ii][jj])
            {
                ii -= 1;
            }
            else
            {
                jj -= 1;
            } // End if.
        } // End while.

        return ((double)(matches * 2) / (file1.length + file2.length));
        // "matches" == Length of LCS; used to calc similarity value between 0 & 1.
    }
}