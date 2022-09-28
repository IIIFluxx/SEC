package edu.curtin.bustimetable;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Controls the main window, displaying the bus timetable contents, a toolbar (with 'load', 'save'
 * and 'add' buttons and a search field) and a status bar that displays the current time.
 *
 * The table actually displays a filtered subset of the timetable entries, according to which 
 * entries match the search field.
 */
public class MainUI
{
    private static final int SPACING = 8;
    
    private Stage stage;
    private ObservableList<TimetableEntry> entries;
    private LoadSaveUI loadSaveUI;
    private AddUI addUI;
    private TextField statusBar = new TextField();
    private Locale locale;
    private ResourceBundle bundle;

    public MainUI(Stage stage, ObservableList<TimetableEntry> entries, LoadSaveUI loadSaveUI, AddUI addUI, Locale locale, ResourceBundle bundle)
    {
        this.stage = stage;
        this.entries = entries;
        this.loadSaveUI = loadSaveUI;
        this.addUI = addUI;
        this.locale = locale;
        this.bundle = bundle;
    }
    
    public void display()
    {

        stage.setTitle(bundle.getString("title_txt"));
        stage.setMinWidth(1000);
                
        // Create toolbar and button event handlers
        //var loadBtn = new Button("Load..."); // Instead... bundle.getString("")
        var loadBtn = new Button(bundle.getString("load_txt"));
        var saveBtn = new Button(bundle.getString("save_txt"));
        var addBtn = new Button(bundle.getString("add_txt"));
        var filterText = new TextField();        
        filterText.setPromptText(bundle.getString("search_txt"));
        ToolBar toolBar = new ToolBar(
            loadBtn, saveBtn, addBtn, new Separator(), filterText);
        loadBtn.setOnAction(event -> loadSaveUI.load());
        saveBtn.setOnAction(event -> loadSaveUI.save());
        addBtn.setOnAction(event -> addUI.addEntry());
        
        // Table and table data
        var entryTable = new TableView<TimetableEntry>();
        var filteredEntries = new FilteredList<>(entries);
        entryTable.setItems(filteredEntries);
        
        filterText.textProperty().addListener(
            (field, oldVal, newVal) ->
                filteredEntries.setPredicate(entry -> matches(entry, newVal))
        );
        
        // Table columns
        TableColumn<TimetableEntry,String> routeIdCol       = new TableColumn<>(bundle.getString("routeid_txt"));
        TableColumn<TimetableEntry,String> fromCol          = new TableColumn<>(bundle.getString("from_txt"));
        TableColumn<TimetableEntry,String> destinationCol   = new TableColumn<>(bundle.getString("destination_txt"));
        TableColumn<TimetableEntry,String> departureTimeCol = new TableColumn<>(bundle.getString("departure_txt"));
        TableColumn<TimetableEntry,String> arrivalTimeCol   = new TableColumn<>(bundle.getString("arrival_txt"));
        TableColumn<TimetableEntry,String> durationCol      = new TableColumn<>(bundle.getString("duration_txt"));
        
        entryTable.getColumns().setAll(List.of(routeIdCol, fromCol, destinationCol, departureTimeCol, arrivalTimeCol, durationCol));
        
        // Formatting table column values
        routeIdCol.setCellValueFactory( 
            (cell) -> new SimpleStringProperty(cell.getValue().getRouteId()) );
            
        fromCol.setCellValueFactory( 
            (cell) -> new SimpleStringProperty(cell.getValue().getFrom()) );
        
        destinationCol.setCellValueFactory(
            (cell) -> new SimpleStringProperty(cell.getValue().getDestination()) );
            
        departureTimeCol.setCellValueFactory(
            (cell) -> new SimpleStringProperty(getDepartureTimeString(cell.getValue())));
                        
        arrivalTimeCol.setCellValueFactory(
            (cell) -> new SimpleStringProperty(getArrivalTimeString(cell.getValue())));
            
        durationCol.setCellValueFactory(
            (cell) -> new SimpleStringProperty(String.valueOf(cell.getValue().getDuration().toMinutes())));
          
        // Table column widths.
        routeIdCol      .prefWidthProperty().bind(entryTable.widthProperty().multiply(0.10));
        fromCol         .prefWidthProperty().bind(entryTable.widthProperty().multiply(0.30));
        destinationCol  .prefWidthProperty().bind(entryTable.widthProperty().multiply(0.30));
        departureTimeCol.prefWidthProperty().bind(entryTable.widthProperty().multiply(0.10));
        arrivalTimeCol  .prefWidthProperty().bind(entryTable.widthProperty().multiply(0.10));
        durationCol     .prefWidthProperty().bind(entryTable.widthProperty().multiply(0.10));            
        
        // Status bar
        var exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(
            () -> Platform.runLater(this::updateStatus), 
            0L, 1L, TimeUnit.SECONDS);
        stage.setOnHiding(event -> exec.shutdown());        
        
        // Add the main parts of the UI to the window.
        var mainBox = new BorderPane();
        mainBox.setTop(toolBar);
        mainBox.setCenter(entryTable);
        mainBox.setBottom(statusBar);
        Scene scene = new Scene(mainBox);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();        
    }
    
    
    private boolean matches(TimetableEntry entry, String searchTerm)
    {
        return entry.getFrom().toLowerCase().contains(searchTerm.toLowerCase()) || 
               entry.getDestination().toLowerCase().contains(searchTerm.toLowerCase());
    }

    private String getDepartureTimeString(TimetableEntry entry)
    {
        return entry.getDepartureTime().toString();
    }
    
    private String getArrivalTimeString(TimetableEntry entry)
    {
        return "";
    }
    
    private void updateStatus()
    {
        statusBar.setText(LocalDateTime.now().toString());
    }
}
