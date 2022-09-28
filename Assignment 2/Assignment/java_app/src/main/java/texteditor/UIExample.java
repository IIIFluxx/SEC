package texteditor;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
/**
 * Classname: UIExample
 * Function: Main class that coordinates all features of this program and its user interface.
 *           Methods split up to their specific uses.
 * Reference: File given to us, and adapted by myself for this assignment.
 * Author: Bharath Sukesh
 * Date: 27/10/21
 */
public class UIExample extends Application implements APIClasses.Control
{
    public static void main(String[] args)
    {
        Application.launch(args);
    }

    private TextArea textArea = new TextArea();
    private ToolBar toolBar = new ToolBar();
    private ObservableList<String> list = FXCollections.observableArrayList();
    private ListView<String> listView = new ListView<>(list);
    private Scene scene;
    private FileChooser fileDialog = new FileChooser();
    private Stage stage;
    private ArrayList<KeyCombination> LOKC = new ArrayList<KeyCombination>();
    private List<TokenData> listOfTokens;
    private ResourceBundle bundle;
    private Dialog<String> encodingDialog;
    private static final int SPACING = 8;
    private FileIO fileIO;
    private Locale locale;

    @Override
    public void start(Stage inStage) {
        var localeString = getParameters().getNamed().get("locale");

        // Default locale IF no string provided.
        locale = Locale.getDefault();
        bundle = ResourceBundle.getBundle("bundle", locale);

        if(localeString != null)
        {
            locale = Locale.forLanguageTag(localeString);
            bundle = ResourceBundle.getBundle("bundle", locale);
        }
        //System.out.println("====== Locale  -----> " + locale.getDisplayCountry());
        // Set up stage.
        stage = inStage;
        stage.setTitle(bundle.getString("title_txt"));
        stage.setMinWidth(800);

        // Create toolbar
        Button loadFilebtn = new Button(bundle.getString("loadfile_txt"));
        Button saveFilebtn = new Button(bundle.getString("savefile_txt"));
        Button addPlugbtn = new Button(bundle.getString("plugscpt_txt"));
        Button loadScptbtn = new Button(bundle.getString("loadscript_txt"));
        Separator separator1 = new Separator(Orientation.HORIZONTAL);
        Separator separator2 = new Separator(Orientation.HORIZONTAL);
        Label lbl = new Label(bundle.getString("loadedplug_lbl"));
        toolBar = new ToolBar(loadFilebtn, saveFilebtn, separator1, addPlugbtn, loadScptbtn, separator2, lbl);

        // Subtle user experience tweaks
        toolBar.setFocusTraversable(false);
        toolBar.getItems().forEach(btn -> btn.setFocusTraversable(false));
        textArea.setStyle("-fx-font-family: 'monospace'"); // Set the font

        // Add the main parts of the UI to the window.
        BorderPane mainBox = new BorderPane();
        mainBox.setTop(toolBar);
        mainBox.setCenter(textArea);
        scene = new Scene(mainBox);

        fileIO = new FileIO();
        // Button event handlers.
        loadFilebtn.setOnAction(event -> loadFileDialog());
        saveFilebtn.setOnAction(event -> saveFileDialog());
        addPlugbtn.setOnAction(event -> showAddDialog());
        loadScptbtn.setOnAction(event -> loadScriptDialog());

        // TextArea event handlers & caret positioning.
        textArea.textProperty().addListener((object, oldValue, newValue) ->
        {
            System.out.println("caret position is " + textArea.getCaretPosition() +
                    "; text is\n---\n" + newValue + "\n---\n");
        });

        textArea.setText("This is some\ndemonstration text\nTry pressing F1, ctrl+b, ctrl+shift+b or alt+b.");
        textArea.selectRange(8, 16); // Select a range of text (and move the caret to the end)
        // =================================================================
        listOfTokens = new ArrayList<>();
        // Read file 'keymap' on startup:
        try
        {
            MyParser parser = new MyParser(new FileReader("keymap", Charset.forName("UTF-8")));
            if(parser != null)
            {
                listOfTokens = parser.helloWorld(listOfTokens);
                System.out.println("Input valid");
            }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File not found, please provide valid keymap file in the appropriate directory");
            System.out.println("at Directory: Assignment/java_app/keymap");
        }
        catch(texteditor.ParseException e)
        {
            System.out.println("Error occurred whilst parsing!");
            System.out.println(e.getMessage());
        }
        catch(TokenMgrError e)
        {
            System.out.println("Lexical error has occurred!");
            System.out.println("Ensure keymap is formatted correctly!");
            System.out.println(e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println("File IO error!");
            System.out.println(e.getMessage());
        }
        addKeyCombo();
        //System.out.println("SIZE of LOKC = " + LOKC.size()); // Works.
        listenKeyCombo();
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    public void addKeyCombo()
    {
        for(int ii=0; ii<listOfTokens.size();ii++)
        {
            // For each key combination....
            //System.out.println("Keys: " + listOfTokens.get(ii).getKeysOnly());
            // == ctrl, +, shift, +, i
            String curSetKeys = listOfTokens.get(ii).getKeysOnly();
            String[] parts = curSetKeys.split(",");
            //NOTE: This is only because List.toString(). **NOT** because of DSL parsing.
            boolean ctrl = false, alt = false, shift = false;
            for(String a : parts)
            {
                if(a.contains("ctrl"))
                {
                    // Set CTRL Boolean to be true.
                    ctrl = true;
                }
                if(a.contains("alt"))
                {
                    // Set ALT Boolean to be true.
                    alt = true;
                }
                if(a.contains("shift"))
                {
                    // Set SHIFT Boolean to be true.
                    shift = true;
                }
                if(a.length() == 1) // There MUST be a letter.
                {
                    char c = a.charAt(0);
                    if(Character.isLowerCase(c))
                    {
                        // Register key as part of the command. KeyCode.getKeyCode(c));
                        KeyCombination newCombo = new KeyCombination(c, ctrl, shift, alt, listOfTokens.get(ii));
                        LOKC.add(newCombo);
                    }
                }
                else if(a.length() != 1 && !a.equals("ctrl") && !a.equals("shift") && !a.equals("alt"))
                {
                    System.out.println("Provided key name '" + a + "' in keymap file is not of length 1");
                }
            }
        }

    }

    public void listenKeyCombo()
    {
        scene.setOnKeyPressed(keyEvent ->
        {
            // See the documentation for the KeyCode class to see all the available keys.
            KeyCode key = keyEvent.getCode();
            boolean ctrl = keyEvent.isControlDown();
            boolean shift = keyEvent.isShiftDown();
            boolean alt = keyEvent.isAltDown();
            System.out.println("keyname = " + key.getName());
            if(key.getName() != "Ctrl" && key.getName() !=  "Alt" && key.getName() != "Shift")
            {
                for(int ii=0;ii<LOKC.size();ii++)
                {
                    if( ctrl == LOKC.get(ii).getControl()
                            && shift == LOKC.get(ii).getShift()
                            && alt == LOKC.get(ii).getAlt())
                    {
                        if(key.getName().length() == 1 && Character.toLowerCase(key.getName().charAt(0)) == LOKC.get(ii).getCharac())
                        {
                            TokenData currentCommand = LOKC.get(ii).getTokens();
                            // Now perform the command that the TokenData requests.
                            performCommand(currentCommand);
                        }
                    }
                }
            }
            // =====================
        });
    }

    // One TokenData = ctrl,+,shift,+,d,delete,"//",at start of line, insdel, content, location.
    public void performCommand(TokenData t)
    {
        String loc = "";
        String insertOrdel = "";
        String trimmed = t.getContent().replace("\"","");
        // Step 1. Determines ins/del.
        if(t.getLocation() == "at start of line")
        {
            loc = "START";
        }
        else if(t.getLocation() == "at caret")
        {
            loc = "CARET";
        }

        // Step 2. Determine SOL/Caret".
        if(t.getInsdel() == "delete")
        {
            insertOrdel = "DELETE";
            deleteText(trimmed, loc);
        }
        else if(t.getInsdel() == "insert")
        {
            insertOrdel = "INSERT";
            insertText(trimmed, loc);
        }
        //System.out.println("\n\n=========\n" + insertOrdel + " " + t.getContent() + " at " + loc+"\n\n==========");
    }

    public void insertText(String text, String loc) // Alt + T --> Alt + S
    {
        int prevCaretPos = textArea.getCaretPosition();
        if(loc == "START")
        {
            // Insert at start of line, means we have to look for the last newline.
            int c = textArea.getCaretPosition();
            String trimmed = textArea.getText().substring(0, textArea.getCaretPosition());
            int newlineIndex = trimmed.lastIndexOf("\n", c); // index parameter = to START the search from.
             // Find last newline before caret position.
            System.out.println("newlineindex = " + newlineIndex);
            if(newlineIndex != -1)
            {
                textArea.setText(new StringBuilder(textArea.getText()).insert(newlineIndex+1, text).toString());
                //newlineIndex +1 because we want to insert AFTER the newline character.
                textArea.positionCaret(prevCaretPos + text.length());
            }
            else // If there isn't a newline character in the line, insert at the start of the line.
            {
                textArea.setText(new StringBuilder(textArea.getText()).insert(0, text).toString());
                textArea.positionCaret(prevCaretPos + text.length());
            }
        }
        else if(loc == "CARET")
        {
            textArea.setText(new StringBuilder(textArea.getText()).insert(textArea.getCaretPosition(), text).toString());
            textArea.positionCaret(prevCaretPos + text.length());
        }
     }

    public void deleteText(String text, String loc)
    {
        int prevCaretPos = textArea.getCaretPosition();
        if(loc == "START")
        {
            int c = textArea.getCaretPosition();
            int l = text.length();
            String trimmed = textArea.getText().substring(0, textArea.getCaretPosition());
            int newlineIndex = trimmed.lastIndexOf("\n", c);
            System.out.println("newlineindex = " + newlineIndex);

            if(newlineIndex != -1) // i.e. new line found on same line as caret.
            {
                // Check substring from new line index --> index + content.length
                String check = textArea.getText().substring(newlineIndex+1, newlineIndex+ 1 + l);
                if(check.equals(text))
                {
                    textArea.setText(new StringBuilder(textArea.getText()).delete(
                            newlineIndex+1, newlineIndex+1+l).toString());
                    textArea.positionCaret(prevCaretPos-text.length());
                }
                else
                {
                    System.out.println("Not located, cannot find " + text + " at the start of the line.");
                }
            }
            else if(newlineIndex == -1) // If no newline found, go to beginning of line and check
            {
                // textarea.text.length determines how long the current line is.
                if(textArea.getText().length() >= text.length()) // e.g., check IF there are 4 spaces *TO DELETE*.
                {
                    // Check substring from start of line index --> index + content.length
                    String check = textArea.getText().substring(0, l);
                    if(check.equals(text))
                    {
                        textArea.setText(new StringBuilder(textArea.getText()).delete(
                                0, l).toString());
                        textArea.positionCaret(prevCaretPos-text.length());
                    }
                }
            }
        }
        else if(loc == "CARET")
        {
            // Check if in front of caret for text.length() if it exists, only if it does, delete.
            int l = text.length();
            int c = textArea.getCaretPosition();
            String t = textArea.getText();
            String check = textArea.getText().substring(c, c+l);
            if(check.equals(text))
            {
                textArea.setText(new StringBuilder(textArea.getText()).delete(textArea.getCaretPosition(), textArea.getCaretPosition() + text.length()).toString());
                textArea.positionCaret(prevCaretPos);
            }
            else
            {
                System.out.println("Not located, cannot find " + text + " in FRONT of Caret position.");
            }
        }
    }

    private void loadFileDialog()
    {
        fileDialog.setTitle(bundle.getString("loadfile_txt"));
        File f = fileDialog.showOpenDialog(stage);
        if(f != null)
        {
            String encoding = getEncoding();
            if(encoding != null)
            {
                System.out.println("Encoding = " + encoding);
                try
                {
                    String textContents = fileIO.getFileContents(f, encoding);
                    textArea.setText(textContents);
                }
                catch(IOException e)
                {
                    new Alert(
                            Alert.AlertType.ERROR,
                            String.format(bundle.getString("loaderror_txt"), e.getClass().getName(), e.getMessage()),
                            ButtonType.CLOSE
                    ).showAndWait();
                }
            }
        }
    }

    private void saveFileDialog()
    {
        fileDialog.setTitle(bundle.getString("savefile_txt"));
        File f = fileDialog.showSaveDialog(stage);
        if(f != null)
        {
            String encoding = getEncoding();
            if(encoding != null)
            {
                try
                {
                    fileIO.save(f, textArea.getText(), encoding);
                }
                catch(IOException e)
                {
                    new Alert(
                            Alert.AlertType.ERROR,
                            String.format(bundle.getString("saveerror_txt"), e.getClass().getName(), e.getMessage()),
                            ButtonType.CLOSE
                    ).showAndWait();
                }
            }
        }
    }

    // Code retrieved from Practical #8 - Internationalization and adapted for Assignment #2.
    private String getEncoding()
    {
        if(encodingDialog == null)
        {
            var encodingComboBox = new ComboBox<String>();
            var content = new FlowPane();
            encodingDialog = new Dialog<>();
            encodingDialog.setTitle(bundle.getString("encodingtype_txt"));
            encodingDialog.getDialogPane().setContent(content);
            encodingDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            encodingDialog.setResultConverter(
                    btn -> (btn == ButtonType.OK) ? encodingComboBox.getValue() : null);

            content.setHgap(SPACING);
            content.getChildren().setAll(new Label(bundle.getString("encoding_txt")), encodingComboBox);

            encodingComboBox.getItems().setAll("UTF-8", "UTF-16", "UTF-32");
            encodingComboBox.setValue("UTF-8");
        }
        return encodingDialog.showAndWait().orElse(null);
    }

    private void loadScriptDialog() {
        fileDialog.setTitle(bundle.getString("loadfile_txt"));
        String emojiScript = "";
        ScriptHandler sH;
        File f = fileDialog.showOpenDialog(stage);
        if(f != null)
        {
            if(f.getName().endsWith("py")) // Ensure it is a Python file we accept.
            {
                String encoding = "UTF-8";
                if(encoding != null)
                {
                    sH = new ScriptHandler();
                    try
                    {
                        emojiScript = fileIO.getFileContents(f, encoding);
                        if(emojiScript.length() != 0)
                        {
                            sH.runScript(this, emojiScript);
                        }
                        else
                        {
                            System.out.println("Script's text content must be greater than length 0!");
                            System.out.println("Please choose a valid python script file.");
                        }
                    } catch (IOException e) {
                        System.out.println("File IO error!");
                        System.out.println(e.getMessage());
                    }
                }
            }
            else
            {
                System.out.println("File must end in .py as only Python files are accepted.");
                System.out.println("Please choose a python script file.");
            }
        }
    }


    private void showAddDialog() // Add Plugins button on Click.
    {
        Button addPluginBtn = new Button(bundle.getString("addplug_txt"));
        ToolBar toolBar = new ToolBar(addPluginBtn);
        addPluginBtn.setOnAction(event -> showPluginDialog());
        // This represents the list of plugins that have been loaded.
        BorderPane box = new BorderPane();
        box.setTop(toolBar);
        box.setCenter(listView);

        Dialog dialog = new Dialog();
        dialog.setTitle(bundle.getString("addplugs_lbl"));
        dialog.setHeaderText(bundle.getString("loadedlist_txt"));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    void showPluginDialog() // Dialog Box for entering plugin classname
    {
        var dialog = new TextInputDialog();
        dialog.setTitle(bundle.getString("addplug_txt"));
        dialog.setHeaderText(bundle.getString("classname_txt"));

        var inputStr = dialog.showAndWait().orElse(null);
        System.out.println("String: " + inputStr);
        if(inputStr != null)
        {
            new Alert(
                    Alert.AlertType.INFORMATION,
                    bundle.getString("entered1_txt") + inputStr + bundle.getString("entered2_txt"),
                    ButtonType.OK).showAndWait();

            // Send to plugin handler - finds method via reflection - return something in Plugin Handler indicating success.
            // If so ---> add to list.
            pluginController pC = new pluginController();
            String success;
            success = pC.addPlugin(inputStr, this, locale);
            if(success.equals("Not found"))
            {
                new Alert(Alert.AlertType.INFORMATION,bundle.getString("notfound_txt"),
                        ButtonType.OK).showAndWait();
            }
            else
            {
                new Alert(Alert.AlertType.INFORMATION,bundle.getString("found_txt"),
                        ButtonType.OK).showAndWait();
                this.list.add(success); // Add to list if plugin found and loaded.
            }
        }
    }

    public Locale getLocale() {return locale;}

    public String takeInput()
    {
        var dialog = new TextInputDialog();
        dialog.setTitle(bundle.getString("inputtitle_txt"));
        dialog.setHeaderText(bundle.getString("query_txt"));
        var inputStr = dialog.showAndWait().orElse(null);
        if(inputStr != null)
        {
            new Alert(
                    Alert.AlertType.INFORMATION,
                    bundle.getString("entered1_txt") + inputStr + bundle.getString("entered2_txt"),
                    ButtonType.OK).showAndWait();
        }
        // Now send the string back
        return inputStr;
    }

    @Override
    public void registerBtnPress(String label, APIClasses.DataCollector callback, APIClasses.InputReceiver inputCallback)
    {
        Button newPlugin = new Button(label);
        this.getToolBar().getItems().add(newPlugin);
        // Make new button, add onclick for the button, goes to callback.
        newPlugin.setOnAction(event ->
            //1. Get string contents, 2. Get caret position.
            // 3. in DSource - modify string at caret position, 4. send back string.
            buttonProcessing(callback,inputCallback)
        );
    }

    private void buttonProcessing(APIClasses.DataCollector callback, APIClasses.InputReceiver inputCallback)
    {
        DataIntermed dateObj = new DataIntermed(textArea.getText(), textArea.getCaretPosition());
        DataIntermed updatedObj = callback.collect(dateObj);
        // NOTE: Setting text area should always happen, even if there's no changes to textarea (not Date plugin).
        textArea.setText(String.valueOf(updatedObj.getText()));
        // We also want to check whether the plugin needs user input to operate.
        if(inputCallback != null && inputCallback.needInput() == true)
        {
            String searchTerm = takeInput();
            //System.out.println("Search term = " + searchTerm);
            updatedObj.setSearchTerm(searchTerm);
            // Now we call inputCallback, provide it with dm, get back dm and reflect that in UI.
            DataIntermed results = callback.collect(updatedObj); // This will go to FindDataSource.
            // Returned back will be the specified indexes where the highlight should take place.
            textArea.selectRange(results.getStartIdx(), results.getEndIdx());
            // Select a range of text (and move the caret to the end). Plugin finished.
        }
        // Else - Do nothing
    }


    public void registerKeyPress(String keyName, APIClasses.DataCollector callback, APIClasses.InputReceiver inputCallback)
    {
        scene.addEventHandler(KeyEvent.ANY, keyEvent ->
        {
            KeyCode key = keyEvent.getCode();
            // Plugin sends in the key it wants to register for
            if (key == KeyCode.getKeyCode(keyName))
            {
                String searchTerm = takeInput();
                System.out.println("Search term = " + searchTerm);
                DataIntermed dm = new DataIntermed(textArea.getText(), getCaret(), searchTerm);
                // Now we call inputCallback, provide it with dm, get back dm and reflect that in UI.
                DataIntermed results = callback.collect(dm); // This will go to FindDataSource.
                // Returned back will be the specified indexes where the highlight should take place.
                textArea.selectRange(results.getStartIdx(), results.getEndIdx());
                // Select a range of text (and move the caret to the end). Plugin finished.
            }
        });
    }

    @Override
    public void registerTextChanged(APIClasses.DataCollector callback)
    {
        textArea.textProperty().addListener((object, oldValue, newValue) ->
        {
            DataIntermed sendObj = new DataIntermed(textArea.getText(), textArea.getCaretPosition());
            DataIntermed dObj;
            dObj = callback.collect(sendObj);
            System.out.println("DOBJ:" + dObj.getCaretPos());
            textArea.setText(String.valueOf(dObj.getText()));
        });
    }

    @Override
    public void addScriptName(String inName) {
        this.list.add(inName);
    }


    public ToolBar getToolBar() {
        return toolBar;
    }
    public int getCaret()
    {
        return textArea.getCaretPosition();
    }
}