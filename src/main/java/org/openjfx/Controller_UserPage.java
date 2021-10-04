package org.openjfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.openjfx.dialogs.Dialogs;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Controller_UserPage implements Initializable {

    Component_Collection_User collection = new Component_Collection_User();
    Component_Collection_User userCollection = new Component_Collection_User();
    List<PC_Component_User> componentList = new ArrayList<>();
    List<PC_Component_User> userList = new ArrayList<>();
    List<PC_Component> sampleList = new ArrayList<>(); //used if default file does not load
    FileChooser fileChooser = new FileChooser();
    BuildChecker checker = new BuildChecker();
    boolean isListFiltered = false;

    @FXML
    private TableView<PC_Component_User> tableView_CompList;

    @FXML
    private TableView<PC_Component_User> tableView_UserItemList;

    @FXML
    private Label label_FilterInfo, label_AddItemInfo, label_TotalPrice, label_FileInfo,
            label_RemoveInfo;

    @FXML
    private TableColumn<PC_Component_User, String> col_Component;

    @FXML
    private TableColumn<PC_Component_User, String> col_Name;

    @FXML
    private TableColumn<PC_Component_User, Double> col_Price;

    @FXML
    private TableColumn<PC_Component_User, String> col_UserItemComponent;

    @FXML
    private TableColumn<PC_Component_User, String> col_UserItemName;

    @FXML
    private TableColumn<PC_Component_User, Double> col_UserItemPrice;

    @FXML
    private ChoiceBox<String> choiceBox_Components;

    @FXML
    private ChoiceBox<String> choiceBox_Price;

    @FXML
    private ChoiceBox<String> choiceBox_SampleBuilds;

    @FXML
    VBox vBox;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBox.setStyle("-fx-background-color:linear-gradient(to right bottom, #126374, #2f7781, #4a8c8d, #65a099, #81b5a5);");

        collection.attach_Comp_to_TableView(tableView_CompList);
        userCollection.attach_Comp_to_TableView(tableView_UserItemList);


        //LOADS SAMPLE LIST FILE "sampleList.bin" ON START
        try {
            Path path = Paths.get("sampleList.bin");
            readFileList(path);
        } catch (IOException | ClassNotFoundException e) {
            loadSampleList();
            for(PC_Component pc : sampleList) {
               collection.add_Component(CompParser.parseUserComp(pc.toString()));
            }
            Dialogs.showErrorDialog("Cannot load sample list file! Using default one!");
        }



        //CONFIGURE CHOICEBOXES
        String[] filterChoices = {"All items", "Processor", "Motherboard", "Graphics card", "Harddisk", "Memory", "Monitor", "Keyboard", "Mouse"};
        choiceBox_Components.getItems().addAll(FXCollections.observableArrayList(filterChoices));
        choiceBox_Components.setValue("All items");

        String[] priceChoices = {"All prices", "Below 1000", "Below 2000", "Below 3000", "Below 4000", "4000 - Above"};
        choiceBox_Price.getItems().addAll(priceChoices);
        choiceBox_Price.setValue("All prices");

        String[] processors = {"Show sample builds","Build1", "Build2", "Build3"};
        choiceBox_SampleBuilds.getItems().addAll(FXCollections.observableArrayList(processors));
        choiceBox_SampleBuilds.setValue("Show sample builds");

        //ADD EVENT  LISTENER TO CBOX
        choiceBox_Components.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> filter(newValue));
        choiceBox_Price.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> filter(choiceBox_Components.getValue()));
        choiceBox_SampleBuilds.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showSampleBuilds(newValue));


    }

    //READS THE SAMPLE FILE "sampleList.bin"
    public void readFileList(Path path) throws IOException, ClassNotFoundException {
        FileReaderBinary reader = new FileReaderBinary();
        List<PC_Component> listFromFile = reader.readFile(path);
        readCompFromFile(listFromFile);
    }

    private void readCompFromFile(List<PC_Component> list) {
        for(PC_Component c : list) {
            PC_Component newComponent = CompParser.parseComp(c.toString());
            PC_Component_User newUserComp = CompParser.parseUserComp(newComponent.toString());
            collection.add_Component(newUserComp);
            componentList.add(newUserComp);
        }

    }


    // ================ ADDING NEW ITEM TO TABLE =========== //
    @FXML
    void add_Item(ActionEvent event) {
        clearLabels();
        label_AddItemInfo.setText("");
       if(tableView_CompList.getSelectionModel().getSelectedItem() == null) {
           label_AddItemInfo.setText("Select an item to add!");
       } else {
           PC_Component_User component_user = tableView_CompList.getSelectionModel().getSelectedItem();
           if(checker.isComponentAdded(userList, component_user)) {
               Dialogs.cannontAddComponent("Remove " + component_user.getComponentType() + " from build list" + "\n" + "then add a new one" );
           } else {
               userCollection.add_Component(component_user);
               userList.add(component_user);
               label_AddItemInfo.setText("Item added!");
               tableView_CompList.getSelectionModel().clearSelection(); //undo selection
               computeTotal(userList);
           }
       }
    }


    // ======= REMOVING ITEMS FROM TABLE ============== //
    public void remove_Item(ActionEvent actionEvent) {
        clearLabels();
        if(tableView_UserItemList.getSelectionModel().getSelectedItem() == null) {
            label_RemoveInfo.setText("Select an item to delete!");
        } else {
            int selectedItem = tableView_UserItemList.getSelectionModel().getSelectedIndex();
            userCollection.delete_Component(selectedItem);
            userList.remove(selectedItem);
            tableView_UserItemList.getSelectionModel().clearSelection();
            label_RemoveInfo.setText("Item removed!");
            computeTotal(userList);
        }
    }

    //============= SAVE LIST AS ".txt" FILE ============================= //
    @FXML
    private void save(ActionEvent actionEvent) {
        if(userList.isEmpty()) {
            Dialogs.listIsEmpty("Cannot save list!");
        } else {
            if(checker.isBuildComplete(userList)) {

                FileChooser fileChooser = new FileChooser();
                //Set extension filter for text files
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);
                //Show save file dialog
                File file = fileChooser.showSaveDialog(null);
                StringBuilder items = new StringBuilder();
                for(PC_Component_User item : userList) {
                    items.append(item.getComponentType()).append(";").append(item.getName()).append(";").append(item.getPrice()).append("\n");
                }

                if (file != null) {
                    saveTextToFile(items.toString(), file);
                    Dialogs.showSuccessDialog("File " + file.getName() + " is saved!");
                    clearLabels();
                }
            } else {
                Dialogs.showMissingComponents(checker.missing(userList));
            }
        }

    }

    private void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(SaveFileWithFileChooser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    //=============  OPEN A SAVED ".txt" FILE ====================================== //
    @FXML
    public void open(ActionEvent actionEvent) {
        configureFileChooser(fileChooser);
        List<File> list = fileChooser.showOpenMultipleDialog(null);
        if (list != null) {
            for (File file : list) {
                openFile(file);
                choiceBox_Components.setValue("All items");
                choiceBox_Price.setValue("All prices");
            }
        }
        clearLabels();
    }

    private void openFile(File file) {
        if(getFileExtension(file).equals("txt")) {
            try {
                Path path = Paths.get(String.valueOf(file));
                List<PC_Component_User> list = new FileReaderTxt().readUserFile(path);
                tableView_UserItemList.getItems().clear();
                for(PC_Component_User pc : list) {
                    if (pc != null) {
                        userCollection.add_Component(pc);
                        userList.add(pc);
                        computeTotal(userList);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(
                        FXMLLoader.class.getName()).log(
                        Level.SEVERE, null, ex
                );
            }
        } else {
            Dialogs.showFileErrorDialog("Not a .txt file!");
        }

    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    private static void configureFileChooser(final FileChooser fileChooser){
        fileChooser.setTitle("Open file");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }
    // ========================================================== //


    // ==================== SHOW SAMPLE BUILDS FROM CHOICE BOX ======================
    private void showSampleBuilds(String txt) {
        clearLabels();
        List<PC_Component_User> testList = userList;
        userList.removeAll(testList);
        tableView_UserItemList.getItems().clear();
        switch (txt) {
            case "Build1":
                loadSampleBuild_1();
                break;
            case "Build2":
                loadSampleBuild_2();
                break;
            case "Build3":
                loadSampleBuild_3();
                break;
            default:
                List<PC_Component_User> testList2 = userList;
                userList.removeAll(testList);
                tableView_UserItemList.getItems().clear();
                break;
        }
    }
    // ============================================================== //


    @FXML
    void clearList(ActionEvent event) {
        List<PC_Component_User> testList = userList;
        userList.removeAll(testList);
        tableView_UserItemList.getItems().clear();
    }


    //COMPUTE TOTAL PRICE
    private void computeTotal(List<PC_Component_User> list) {
        double sum = 0;
        for(PC_Component_User item : list) {
            sum += item.getPrice();
        }
        DecimalFormat df = new DecimalFormat("#.##");
        label_TotalPrice.setText("Total Price: " + df.format(sum) + " NOK");
    }

    // ========================================= FILTERING TABLE =============================================== //
    private void filter(String txt) {
        if(txt.equals("Processor")) {
            label_FilterInfo.setText("");
            List<PC_Component_User> filteredList = filterProcessors(txt, choiceBox_Price.getValue());
            if(filteredList.isEmpty()) {
                tableView_CompList.getItems().clear();
                label_FilterInfo.setText("No Processors within the" + "\n" +  "selected price range");
            }
            filteredList.forEach(i -> tableView_CompList.setItems((ObservableList<PC_Component_User>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("Harddisk")) {
            label_FilterInfo.setText("");
            List<PC_Component_User> filteredList = filterHarddisks(txt, choiceBox_Price.getValue());
            if(filteredList.isEmpty()) {
                tableView_CompList.getItems().clear();
                label_FilterInfo.setText("No Harddisks within the" + "\n" + "selected price range");
            }
            filteredList.forEach(i -> tableView_CompList.setItems((ObservableList<PC_Component_User>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("Memory")) {
            label_FilterInfo.setText("");
            List<PC_Component_User> filteredList = filterMemory(txt, choiceBox_Price.getValue());
            if(filteredList.isEmpty()) {
                tableView_CompList.getItems().clear();
                label_FilterInfo.setText("No Memories within the" + "\n" + "selected price range");
            }
            filteredList.forEach(i -> tableView_CompList.setItems((ObservableList<PC_Component_User>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("Monitor")) {
            label_FilterInfo.setText("");
            List<PC_Component_User> filteredList = filterMonitors(txt, choiceBox_Price.getValue());
            if(filteredList.isEmpty()) {
                tableView_CompList.getItems().clear();
                label_FilterInfo.setText("No Monitors within the" + "\n" + "selected price range");
            }
            filteredList.forEach(i -> tableView_CompList.setItems((ObservableList<PC_Component_User>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("Graphics card")) {
            label_FilterInfo.setText("");
            List<PC_Component_User> filteredList = filterGraphicCard(txt, choiceBox_Price.getValue());
            if(filteredList.isEmpty()) {
                tableView_CompList.getItems().clear();
                label_FilterInfo.setText("No Graphic cards within the" + "\n" + "selected price range");
            }
            filteredList.forEach(i -> tableView_CompList.setItems((ObservableList<PC_Component_User>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("Motherboard")) {
            label_FilterInfo.setText("");
            List<PC_Component_User> filteredList = filterMotherboards(txt, choiceBox_Price.getValue());
            if(filteredList.isEmpty()) {
                tableView_CompList.getItems().clear();
                label_FilterInfo.setText("No Motherboards within the" + "\n" + "selected price range");
            }
            filteredList.forEach(i -> tableView_CompList.setItems((ObservableList<PC_Component_User>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("Keyboard")) {
            label_FilterInfo.setText("");
            List<PC_Component_User> filteredList = filterKeyboards(txt, choiceBox_Price.getValue());
            if(filteredList.isEmpty()) {
                tableView_CompList.getItems().clear();
                label_FilterInfo.setText("No Keyboard within the" + "\n" + "selected price range");
            }
            filteredList.forEach(i -> tableView_CompList.setItems((ObservableList<PC_Component_User>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("Mouse")) {
            label_FilterInfo.setText("");
            List<PC_Component_User> filteredList = filterMouse(txt, choiceBox_Price.getValue());
            if(filteredList.isEmpty()) {
                tableView_CompList.getItems().clear();
                label_FilterInfo.setText("No Mouse within the" + "\n" + "selected price range");
            }
            filteredList.forEach(i -> tableView_CompList.setItems((ObservableList<PC_Component_User>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("All items")) {
            label_FilterInfo.setText("");
            List<PC_Component_User> filteredList = allItems(txt, choiceBox_Price.getValue());
            if(filteredList.isEmpty()) {
                tableView_CompList.getItems().clear();
                label_FilterInfo.setText("No items within the" + "\n" + "selected price range");
            }
            filteredList.forEach(i -> tableView_CompList.setItems((ObservableList<PC_Component_User>)filteredList));
            isListFiltered = true;
        }


    }

    private List<PC_Component_User> filterProcessors(String txt, String price) {
        List<PC_Component_User> filteredList;
        switch (price) {
            case "Below 1000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Processor")).filter(c -> (c.getPrice() < 1000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 2000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Processor")).filter(c -> (c.getPrice() < 2000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 3000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Processor")).filter(c -> (c.getPrice() < 3000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 4000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Processor")).filter(c -> (c.getPrice() < 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "4000 - Above":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Processor")).filter(c -> (c.getPrice() >= 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            default:
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Processor")).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
        }
        return filteredList;
    }

    private List<PC_Component_User> filterHarddisks(String txt, String price) {
        List<PC_Component_User> filteredList;
        switch (price) {
            case "Below 1000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Harddisk")).filter(c -> (c.getPrice() < 1000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 2000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Harddisk")).filter(c -> (c.getPrice() < 2000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 3000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Harddisk")).filter(c -> (c.getPrice() < 3000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 4000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Harddisk")).filter(c -> (c.getPrice() < 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "4000 - Above":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Harddisk")).filter(c -> (c.getPrice() >= 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            default:
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Harddisk")).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
        }

        return filteredList;
    }

    private List<PC_Component_User> filterMemory(String txt, String price) {
        List<PC_Component_User> filteredList;
        switch (price) {
            case "Below 1000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Memory")).filter(c -> (c.getPrice() < 1000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 2000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Memory")).filter(c -> (c.getPrice() < 2000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 3000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Memory")).filter(c -> (c.getPrice() < 3000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 4000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Memory")).filter(c -> (c.getPrice() < 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "4000 - Above":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Memory")).filter(c -> (c.getPrice() >= 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            default:
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Memory")).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
        }

        return filteredList;
    }

    private List<PC_Component_User> filterMonitors(String txt, String price) {
        List<PC_Component_User> filteredList;
        switch (price) {
            case "Below 1000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Monitor")).filter(c -> (c.getPrice() < 1000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 2000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Monitor")).filter(c -> (c.getPrice() < 2000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 3000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Monitor")).filter(c -> (c.getPrice() < 3000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 4000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Monitor")).filter(c -> (c.getPrice() < 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "4000 - Above":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Monitor")).filter(c -> (c.getPrice() >= 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            default:
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Monitor")).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
        }

        return filteredList;
    }

    private List<PC_Component_User> filterGraphicCard(String txt, String price) {
        List<PC_Component_User> filteredList;
        switch (price) {
            case "Below 1000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Graphics card")).filter(c -> (c.getPrice() < 1000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 2000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Graphics card")).filter(c -> (c.getPrice() < 2000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 3000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Graphics card")).filter(c -> (c.getPrice() < 3000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 4000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Graphics card")).filter(c -> (c.getPrice() < 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "4000 - Above":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Graphics card")).filter(c -> (c.getPrice() >= 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            default:
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Graphics card")).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
        }

        return filteredList;
    }

    private List<PC_Component_User> filterMotherboards(String txt, String price) {
        List<PC_Component_User> filteredList;
        switch (price) {
            case "Below 1000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Motherboard")).filter(c -> (c.getPrice() < 1000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 2000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Motherboard")).filter(c -> (c.getPrice() < 2000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 3000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Motherboard")).filter(c -> (c.getPrice() < 3000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 4000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Motherboard")).filter(c -> (c.getPrice() < 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "4000 - Above":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Motherboard")).filter(c -> (c.getPrice() >= 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            default:
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Motherboard")).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
        }

        return filteredList;
    }

    private List<PC_Component_User> filterKeyboards(String txt, String price) {
        List<PC_Component_User> filteredList;
        switch (price) {
            case "Below 1000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Keyboard")).filter(c -> (c.getPrice() < 1000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 2000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Keyboard")).filter(c -> (c.getPrice() < 2000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 3000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Keyboard")).filter(c -> (c.getPrice() < 3000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 4000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Keyboard")).filter(c -> (c.getPrice() < 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "4000 - Above":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Keyboard")).filter(c -> (c.getPrice() >= 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            default:
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Keyboard")).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
        }

        return filteredList;
    }

    private List<PC_Component_User> filterMouse(String txt, String price) {
        List<PC_Component_User> filteredList;
        switch (price) {
            case "Below 1000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Mouse")).filter(c -> (c.getPrice() < 1000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 2000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Mouse")).filter(c -> (c.getPrice() < 2000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 3000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Mouse")).filter(c -> (c.getPrice() < 3000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 4000":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Mouse")).filter(c -> (c.getPrice() < 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "4000 - Above":
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Mouse")).filter(c -> (c.getPrice() >= 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            default:
                filteredList = componentList.stream().filter(c -> c.getComponentType().equals("Mouse")).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
        }

        return filteredList;
    }

    private List<PC_Component_User> allItems(String txt, String price) {
        List<PC_Component_User> filteredList;
        switch (price) {
            case "Below 1000":
                filteredList = componentList.stream().filter(c -> (c.getPrice() < 1000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 2000":
                filteredList = componentList.stream().filter(c -> (c.getPrice() < 2000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 3000":
                filteredList = componentList.stream().filter(c -> (c.getPrice() < 3000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "Below 4000":
                filteredList = componentList.stream().filter(c -> (c.getPrice() < 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            case "4000 - Above":
                filteredList = componentList.stream().filter(c -> (c.getPrice() >= 4000)).collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
            default:
                filteredList = componentList.stream().collect(Collectors.toCollection(FXCollections::observableArrayList));
                break;
        }

        return filteredList;
    }
    // ======================================================================================================================================= //



    // ====================================  LOADS THIS DATA IF SAMPLE LIST "sampleList.bin" FAILS TO LOAD ================================= //
    private void loadSampleList() {
        PC_Component pc1 = new PC_Component("Processor", "AMD Ryzen 5 3600", 1837.58);
        Processor p1 = new Processor(pc1, 2.6);
        sampleList.add(p1);

        PC_Component pc2 = new PC_Component("Processor", "AMD Ryzen 7 3700X", 3018.66);
        Processor p2 = new Processor(pc1, 2.6);
        sampleList.add(p2);

        PC_Component pc3 = new PC_Component("Processor", "AMD Ryzen 9 3900X",4353.45);
        Processor p3 = new Processor(pc1, 3.8);
        sampleList.add(p3);

        PC_Component pc4 = new PC_Component("Processor", "Intel Core i5-9600K",3891.41);
        Processor p4 = new Processor(pc1, 3.7);
        sampleList.add(p4);

        PC_Component pc5 = new PC_Component("Graphics card", "MSI GeForce GTX 1050 Ti", 1552.54);
        sampleList.add(pc5);

        PC_Component pc6 = new PC_Component("Graphics card", "Asus GeForce GTX 1650", 1634.8);
        sampleList.add(pc6);

        PC_Component pc7 = new PC_Component("Graphics card", "Gigabyte GeForce GTX 1650", 1647.9);
        sampleList.add(pc7);

        PC_Component pc8 = new PC_Component("Graphics card", "EVGA GeForce GTX 1050 3 GB", 1647.9);
        sampleList.add(pc8);

        PC_Component pc9 = new PC_Component("Harddisk", "Seagate Barracuda Compute", 555.66);
        Harddisk d1 = new Harddisk(pc9, 2000);
        sampleList.add(d1);

        PC_Component pc10 = new PC_Component("Harddisk", "Samsung 860 Evo", 946.67);
        Harddisk d2 = new Harddisk(pc9, 500);
        sampleList.add(d2);

        PC_Component pc11 = new PC_Component("Harddisk", "Western Digital Blue", 586.52);
        Harddisk d3 = new Harddisk(pc9, 2000);
        sampleList.add(d3);

        PC_Component pc12 = new PC_Component("Monitor", "Asus VG248QE ", 2561.96 );
        Monitor m1 = new Monitor(pc12, 24.0);
        sampleList.add(m1);

        PC_Component pc13 = new PC_Component("Monitor", "BenQ ZOWIE XL2411P", 2047.51 );
        Monitor m2 = new Monitor(pc12, 24.0);
        sampleList.add(m2);

        PC_Component pc14 = new PC_Component("Monitor", "Asus ROG SWIFT PG27UQ", 2047.51 );
        Monitor m3 = new Monitor(pc12, 27.0);
        sampleList.add(m3);

        PC_Component pc15 = new PC_Component("Monitor", "Acer GN246HL", 2097.22 );
        Monitor m4 = new Monitor(pc12, 24.0);
        sampleList.add(m4);

        PC_Component pc16 = new PC_Component("Keyboard", "Corsair K55 RGB", 514.0);
        sampleList.add(pc16);

        PC_Component pc17 = new PC_Component("Keyboard", "Razer Cynosa Chroma Gaming Keyboard", 462.62);
        sampleList.add(pc17);

        PC_Component pc18 = new PC_Component("Keyboard", "Logitech K120", 92.45);
        sampleList.add(pc18);

        PC_Component pc19 = new PC_Component("Keyboard", "SteelSeries Apex Pro", 2044.0);
        sampleList.add(pc19);

        PC_Component pc20 = new PC_Component("Mouse", "Logitech G Pro Wireless Wireless", 1540.0);
        sampleList.add(pc20);

        PC_Component pc21 = new PC_Component("Mouse", "Corsair Harpoon RGB", 256.49);
        sampleList.add(pc21);

        PC_Component pc22 = new PC_Component("Mouse", "Razer DeathAdder Elite", 410.0);
        sampleList.add(pc22);

        PC_Component pc23 = new PC_Component("Mouse", "SteelSeries RIVAL 650", 1220.9);
        sampleList.add(pc23);

        PC_Component pc24 = new PC_Component("Memory", "Corsair Vengeance LPX", 821.24);
        Memory mry1 = new Memory(pc24, 16);
        sampleList.add(mry1);

        PC_Component pc25 = new PC_Component("Memory", "G.Skill Ripjaws V", 800.0);
        Memory mry2 = new Memory(pc24, 16);
        sampleList.add(mry2);

        PC_Component pc26 = new PC_Component("Memory", "G.Skill Trident Z Neo", 1927.34);
        Memory mry3 = new Memory(pc24, 32);
        sampleList.add(mry3);

        PC_Component pc27 = new PC_Component("Memory", "Crucial Ballistix", 349.04);
        Memory mry4 = new Memory(pc24, 8);
        sampleList.add(mry4);

        PC_Component pc28 = new PC_Component("Motherboard", "ASRock AB350M", 2450.6);
        sampleList.add(pc28);

        PC_Component pc29 = new PC_Component("Motherboard", "Asus PRIME H310M-E R2.0", 574.0);
        sampleList.add(pc29);

        PC_Component pc30 = new PC_Component("Motherboard", "MSI Z170A GAMING M5", 4009.15);
        sampleList.add(pc30);

        PC_Component pc31 = new PC_Component("Motherboard", "Gigabyte GA-78LMT-USB3", 3352.34);
        sampleList.add(pc31);

    }

    private void loadSampleBuild_1() {

        PC_Component_User p1 = new PC_Component_User("Processor","AMD Ryzen 5 3600 2.6Ghz",1837.58);
        PC_Component_User p2 = new PC_Component_User("Graphics card","MSI GeForce GTX 1050 Ti",1552.54);
        PC_Component_User p3 = new PC_Component_User("Harddisk","Seagate Barracuda Compute 2000Gb",555.66);
        PC_Component_User p4 = new PC_Component_User("Monitor","Asus VG248QE  24.0 inches",2561.96);
        PC_Component_User p5 = new PC_Component_User("Keyboard","Corsair K55 RGB",514.0);
        PC_Component_User p6 = new PC_Component_User("Memory","Corsair Vengeance LPX 32Gb",1540.0);
        PC_Component_User p7 = new PC_Component_User("Mouse","Logitech G Pro Wireless Wireless",821.24);
        PC_Component_User p8 = new PC_Component_User("Motherboard","ASRock AB350M",2450.6);

        userList.add(p1);
        userList.add(p2);
        userList.add(p3);
        userList.add(p4);
        userList.add(p5);
        userList.add(p6);
        userList.add(p7);
        userList.add(p8);

        for(PC_Component_User pc : userList) {
            userCollection.add_Component(pc);
            tableView_UserItemList.refresh();
        }

    }

    private void loadSampleBuild_2() {

        PC_Component_User p1 = new PC_Component_User("Processor","AMD Ryzen 5 3600 3.7Ghz",1837.58);
        PC_Component_User p2 = new PC_Component_User("Graphics card","Gigabyte GeForce GTX 1650",1647.9);
        PC_Component_User p3 = new PC_Component_User("Harddisk","Seagate Barracuda Compute 2000Gb",555.66);
        PC_Component_User p4 = new PC_Component_User("Monitor","Asus VG248QE  24.0 inches",2561.96);
        PC_Component_User p5 = new PC_Component_User("Keyboard","SteelSeries Apex Pro",2044.0);
        PC_Component_User p6 = new PC_Component_User("Memory","Corsair Vengeance LPX 16Gb",1540.0);
        PC_Component_User p7 = new PC_Component_User("Mouse","SteelSeries RIVAL 650",821.24);
        PC_Component_User p8 = new PC_Component_User("Motherboard","Gigabyte GA-78LMT-USB3",3352.34);

        userList.add(p1);
        userList.add(p2);
        userList.add(p3);
        userList.add(p4);
        userList.add(p5);
        userList.add(p6);
        userList.add(p7);
        userList.add(p8);

        for(PC_Component_User pc : userList) {
            userCollection.add_Component(pc);
            tableView_UserItemList.refresh();
        }
    }

    private void loadSampleBuild_3() {

        PC_Component_User p1 = new PC_Component_User("Processor","Intel Core i5-9600K 3.7Ghz",3891.41);
        PC_Component_User p2 = new PC_Component_User("Graphics card","Asus GeForce GTX 1650",1634.8);
        PC_Component_User p3 = new PC_Component_User("Harddisk","Seagate Barracuda Compute 2000Gb",555.66);
        PC_Component_User p4 = new PC_Component_User("Monitor","Asus VG248QE  24.0 inches",2561.96);
        PC_Component_User p5 = new PC_Component_User("Keyboard","Razer Cynosa Chroma Gaming Keyboard",462.62);
        PC_Component_User p6 = new PC_Component_User("Memory","Corsair Vengeance LPX 32Gb",821.24);
        PC_Component_User p7 = new PC_Component_User("Mouse","Logitech G Pro Wireless Wireless",1540.0);
        PC_Component_User p8 = new PC_Component_User("Motherboard","MSI Z170A GAMING M5",4009.15);

        userList.add(p1);
        userList.add(p2);
        userList.add(p3);
        userList.add(p4);
        userList.add(p5);
        userList.add(p6);
        userList.add(p7);
        userList.add(p8);

        for(PC_Component_User pc : userList) {
            userCollection.add_Component(pc);
            tableView_UserItemList.refresh();
        }
    }

    private void clearLabels() {
        label_FileInfo.setText("");
        label_AddItemInfo.setText("");
        label_RemoveInfo.setText("");

    }



}