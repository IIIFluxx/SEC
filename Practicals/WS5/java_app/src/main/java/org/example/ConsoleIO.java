package org.example;

import java.util.LinkedList;
import java.util.List;

public class ConsoleIO
{
    static
    {
        System.loadLibrary("example_c_library");
        // Lines up with library basename in C Folder build.gradle
    }

    // Harness
    public static void main(String[] args)
    {
        double ans1;
        List<String> list = new LinkedList<String>();

        // Test #1: read()
        ans1 = read(5.0);
        System.out.println("Default value for read() =  5.0");
        System.out.println("Answer from read() = " + ans1);

        // Test #2: read()
        ans1 = read(5.0);
        System.out.println("Default value for read() =  5.0");
        System.out.println("Answer from read() = " + ans1);

        // Test #3: printStr()
        printStr("WillThisPrint?");
        printStr("And will this second string print?");

        // Test #4: printList()
        list.add("ABC");
        list.add("DEF");
        list.add("GHI");
        list.add("JKL");
        list.add("MNO");
        list.add("PQR");
        list.add("STU");
        list.add("VWX");
        list.add("YZA");

        printList(list);

        System.out.println("All tests conducted.");
    }

    public native static double read(double defaultValue);
    public native static void printStr(String text);
    public native static void printList(List<String> list);
}