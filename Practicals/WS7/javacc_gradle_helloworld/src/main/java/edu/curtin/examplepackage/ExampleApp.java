package edu.curtin.examplepackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class ExampleApp
{
    public static void main(String[] args) throws FileNotFoundException {

        System.out.println(System.getProperty("user.dir"));
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter file name to be validated. Press Ctrl-D to finish.");
        String fileName = sc.nextLine();
        MyParser parser = new MyParser(new FileReader(fileName));
        System.out.println("Filename = " + fileName);
        try
        {
            parser.helloWorld();
            System.out.println("Input valid");
        }
        catch(ParseException e)
        {
            System.out.println("Parsing error!");
            System.out.println(e.getMessage());
        }
    }
}

