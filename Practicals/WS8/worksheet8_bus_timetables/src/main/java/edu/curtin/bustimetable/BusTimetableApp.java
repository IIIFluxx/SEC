package edu.curtin.bustimetable;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * Entry point for the bus timetabling app. It can be invoked with the command-line parameter 
 * --locale=[value].
 */
public class BusTimetableApp extends Application
{
    public static void main(String[] args)
    {
        Application.launch(args);
    }
        
    @Override
    public void start(Stage stage)
    {
        var localeString = getParameters().getNamed().get("locale");

        // Default locale IF no string provided.
        Locale locale = new Locale("en", "AU");
        ResourceBundle bundle = ResourceBundle.getBundle("bundle", locale);

        if(localeString != null)
        {
            // Use this to create a Locale object:
            // Form: --locale=[value]
            if(localeString.equals("en") || localeString.equals("do")) // Currently supported languages;
            // add to if statement if more languages to support.
            {
                locale = new Locale(localeString);
                bundle = ResourceBundle.getBundle("bundle", locale);
            }
            else
            {
                System.out.println("Error has occurred; locale not found");
            }
        }

        var entries = FXCollections.<TimetableEntry>observableArrayList();
        FileIO fileIO = new FileIO();
        LoadSaveUI loadSaveUI = new LoadSaveUI(stage, entries, fileIO, locale, bundle);
        AddUI addUI = new AddUI(entries, locale, bundle);
        new MainUI(stage, entries, loadSaveUI, addUI, locale, bundle).display();
    }
}
