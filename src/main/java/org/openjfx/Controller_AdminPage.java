package org.openjfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.openjfx.dialogs.Dialogs;
import org.openjfx.exceptions.InvalidCoreSpeedException;
import org.openjfx.exceptions.InvalidHDCapacityException;
import org.openjfx.exceptions.InvalidRamCapacityException;
import org.openjfx.exceptions.InvalidScreenSizeException;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller_AdminPage implements Initializable {
    Component_Collection collection = new Component_Collection();
    List<PC_Component> componentList = new ArrayList<>();
    List<PC_Component> sampleList = new ArrayList<>(); //used if default file does not load
    private final IntegerStringConverter integerStringConverter = new IntegerStringConverter();
    private final DoubleStringConverter doubleStringConverter = new DoubleStringConverter();
    boolean isListFiltered = false;

    GeneralComponent_Collection collectionTest = new GeneralComponent_Collection();
    List<GeneralComponent> generalComponentList = new ArrayList<>();



    @FXML
    Label label_Info, label_FilterInfo, lable_DeleteInfo, label_UseList;

    @FXML
    private TextField description_Input, price_Input, coreClock_Input,
            capacity_Input, screenSize_Input;

    @FXML
    private ChoiceBox<String> cBox_Filter, cBox_PriceFilter, cBox_chooseComponent, cBox_Brand;


    @FXML
    private Button btn_SaveList;

    @FXML
    private Button btn_deleteItem;

    @FXML
    TableView<PC_Component> tableView_ComponentList;


    @FXML
    TableColumn<PC_Component, String> col_Component;

    @FXML
    TableColumn< PC_Component, String> col_Name;

    @FXML
    TableColumn<PC_Component, Integer> col_Capacity;

    @FXML
    TableColumn<PC_Component, Double> col_CoreClock;

    @FXML
    TableColumn<PC_Component, Double> col_ScreenSize;

    @FXML
    TableColumn<PC_Component, Double> col_Price;

    @FXML
    AnchorPane anchPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchPane.setStyle("-fx-background-color:linear-gradient(to right bottom, #126374, #2f7781, #4a8c8d, #65a099, #81b5a5);");
        label_UseList.setText("Current opened list will be displayed" + "\n" +  "on customer window.");


        try {
            Path path = Paths.get("sampleList.bin");
            openFile(path);
        } catch (IOException | ClassNotFoundException e) {
            loadSampleList();
            for(PC_Component pc : sampleList) {
                collection.attach_Comp_to_TableView(tableView_ComponentList);
            }
            Dialogs.showFileErrorDialog("Cannot open sample file list!" + "\n" +
                    "Using default item list.");
        }



        collection.attach_Comp_to_TableView(tableView_ComponentList);

        //CONFIGURE CHOICE BOXES FOR FILTERTING AND CHOOSING COMPONENT TYPES
        String[] choices = {"Processor", "Motherboard", "Graphics card", "Harddisk", "Memory", "Monitor", "Keyboard", "Mouse"};
        String[] filterChoices = {"View all items", "Processor", "Motherboard", "Graphics card", "Harddisk", "Memory", "Monitor", "Keyboard", "Mouse"};
        cBox_chooseComponent.getItems().addAll(FXCollections.observableArrayList(choices));
        cBox_Filter.getItems().addAll(FXCollections.observableArrayList(filterChoices));
        cBox_Filter.setValue("View all items");
        String[] priceChoices = {"Filter by price (All prices)", "Below 1000", "Below 2000", "Below 3000", "Below 4000", "4000 - Above"};
        cBox_PriceFilter.getItems().addAll(priceChoices);
        cBox_PriceFilter.setValue("Filter by price (All prices)");

        String[] processorBrands = {"Intel", "AMD"};
        cBox_Brand.getItems().addAll((processorBrands));
        cBox_Brand.setValue(processorBrands[0]);



        //ADD EVENT LISTENER TO CHOICE BOX
        cBox_chooseComponent.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changeBrandChoices());

        cBox_Filter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> filter(newValue));

        cBox_PriceFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> filter(cBox_Filter.getValue()));



        //DEFAULT DISPLAYED TEXT FIELDS
        cBox_chooseComponent.setValue("Processor");
        capacity_Input.setVisible(false);
        screenSize_Input.setVisible(false);

        //ADD STRING CONVERTER TO TABLE COLUMNS AND ZERO VALUES DISPLAYED AS "--"
        col_Price.setCellFactory( TextFieldTableCell.forTableColumn(doubleStringConverter));
        col_Capacity.setCellFactory(tc -> new TextFieldTableCell<PC_Component, Integer>(integerStringConverter) {
            @Override
             public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty) ;
                if (empty) {
                    setText(null);
                } else {
                    if(item != null) {
                        int value = item;
                        if (value <= 0) {
                            setText("--");
                        } else {
                            setText(Integer.toString(value));
                        }
                    } else {
                        tableView_ComponentList.refresh();
                    }

                }
            }


        });
        col_CoreClock.setCellFactory(tc -> new TextFieldTableCell<PC_Component, Double>(doubleStringConverter) {
            @Override
            public void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty) ;
                if (empty) {
                    setText(null);
                } else {
                    if(item != null) {
                        double value = item;
                        if (value <= 0) {
                            setText("--");
                        } else {
                            setText(Double.toString(value));
                        }
                    } else {
                        tableView_ComponentList.refresh();
                    }
                }
            }


        });
        col_ScreenSize.setCellFactory(tc -> new TextFieldTableCell<PC_Component, Double>(doubleStringConverter) {
            @Override
            public void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty) ;
                if (empty) {
                    setText(null);
                } else {
                    if(item != null) {
                        double value = item;
                        if (value <= 0) {
                            setText("--");
                        } else {
                            setText(Double.toString(value));
                        }
                    } else {
                        tableView_ComponentList.refresh();
                    }
                }
            }

        });

    }

    // ============== FOR LOADING THE "sampleList.bin" FILE
    public void openFile(Path path) throws IOException, ClassNotFoundException {
        FileReaderBinary reader = new FileReaderBinary();
        List<PC_Component> listFromFile = reader.readFile(path);
        readCompFromFile(listFromFile);

    }
    private void readCompFromFile(List<PC_Component> list) {
        GeneralComponent g = new GeneralComponent();
        for(PC_Component c : list) {
            PC_Component newComponent = CompParser.parseComp(c.toString());
            collection.add_Component(newComponent);
            componentList.add(newComponent);

        }
    }


    // =========== ADDING NEW ITEM TO TABLE =============== //
    @FXML
    void add_Item(ActionEvent actionEvent) {
        label_Info.setText("");
        PC_Component component = construct_Component();
        if(component != null) {
            if(!isListFiltered) {
                tableView_ComponentList.getItems().clear();
                componentList.add(component);
                componentList.forEach(c -> collection.add_Component(c));
                Dialogs.showSuccessDialog("Item successfully added shown at the bottom!");
            } else {
                addItem_StreamedList(component);
                Dialogs.showSuccessDialog("Item successfully added shown at the bottom!");
            }

        }

    }
    public void addItem_StreamedList(PC_Component comp) { //Adding new item if table list is filtered
        componentList.add(comp);
        ObservableList<PC_Component> updatedList = componentList.stream().collect(Collectors.toCollection(FXCollections::observableArrayList));
        updatedList.forEach(i -> tableView_ComponentList.setItems(updatedList));
    }



    // ==================  DELETE AN ITEM ======== //
    @FXML
    void delete_Item(ActionEvent event) {
        if(tableView_ComponentList.getSelectionModel().getSelectedItem() == null) {
            lable_DeleteInfo.setText("Select an item to delete!");
        } else {
            if(!isListFiltered) {
                int selectedItem = tableView_ComponentList.getSelectionModel().getSelectedIndex();
                collection.delete_Component(selectedItem);
                tableView_ComponentList.getSelectionModel().clearSelection();
                lable_DeleteInfo.setText("Item deleted!");
            } else {
                PC_Component selectedItem = tableView_ComponentList.getSelectionModel().getSelectedItem();
                componentList.remove(selectedItem);
                ObservableList<PC_Component> editedList = componentList.stream().collect(Collectors.toCollection(FXCollections::observableArrayList));
                editedList.forEach(i -> tableView_ComponentList.setItems(editedList));
                tableView_ComponentList.getSelectionModel().clearSelection();
                cBox_Filter.setValue("View all items");
                cBox_PriceFilter.setValue("Filter by price (All prices)");
                Dialogs.showSuccessDialog("Item is deleted!");
            }
        }

    }

    // ==================== TO SET CURRENT OPENED LIST TO BE DISPLAYED ON USER WINDOW ================= //
    @FXML
    void use_List(ActionEvent event) {
        if(!componentList.isEmpty()) {
            FileSaverBinary saver =  new FileSaverBinary();
            Path path = Paths.get("sampleList.bin");

            File file = new File(String.valueOf(path));
            if(file.exists()) {
                System.out.println("File exists");
                try {
                    Files.delete(path);
                    try {
                        saver.saveFileBinary(path).writeObject(componentList);
                        Dialogs.showSuccessDialog("List will be used on customer's page!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Dialogs.showErrorDialog("There are no opened list!");
        }
    }

    // ============== SAVE LIST AS ".bin" FILE =========================== //
    @FXML
    public void saveToPC(ActionEvent actionEvent) {
        if(!componentList.isEmpty()) {
            FileChooser fileChooser = new FileChooser();

            //Set extension filter for text files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Binary files (*.bin)", "*.bin");
            fileChooser.getExtensionFilters().add(extFilter);

            //Show save file dialog
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                saveTextToFile(file);
                Dialogs.showSuccessDialog("File " + file.getName() + " is saved!");
            }
        } else {
            Dialogs.showErrorDialog("List is empty!");
        }

    }

    private void saveTextToFile(File file) {

            FileSaverBinary saver =  new FileSaverBinary();
            Path path = Paths.get(String.valueOf(file));
            try {
                saver.saveFileBinary(path).writeObject(componentList);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }




    //OPEN FILE FROM PC
    FileChooser fileChooser = new FileChooser();
    @FXML
    public void openFromPc(ActionEvent event) {
        configureFileChooser(fileChooser);
        List<File> list = fileChooser.showOpenMultipleDialog(null);
        if (list != null) {
            for (File file : list) {
                openPcFile(file);
            }
        }
    }

    private void openPcFile(File file) {
        if(getFileExtension(file).equals("bin")) {
            try {
                Path path = Paths.get(String.valueOf(file));
                FileReaderBinary reader = new FileReaderBinary();
                List<PC_Component> listFromFile = reader.readFile(path);
                tableView_ComponentList.getItems().clear();
                readCompFromFile(listFromFile);


            } catch (IOException | ClassNotFoundException ex) {
                Dialogs.showFileErrorDialog("File has invalid characters");
            }
        }  else {
           Dialogs.showFileErrorDialog("Not a .bin file!");
        }

    }

    private static void configureFileChooser(final FileChooser fileChooser){
        fileChooser.setTitle("Open file");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    private PC_Component construct_Component() {
        String componentType = cBox_chooseComponent.getValue();
        String description = cBox_Brand.getValue() + " " +  description_Input.getText();
        double price = 0.0;
        double coreClock = 0.0;
        double screenSize = 0.0;
        int capacity = 0;
        PC_Component comp = null;
        boolean canCreate = true;


        if(checkEmptyFields()) {
            label_Info.setText("Some fields required missing");
        } else {
            //CREATE NEW KEYBOARD || MOUSE || GRAPHICS CARD
            if(cBox_chooseComponent.getValue().equals("Keyboard") || cBox_chooseComponent.getValue().equals("Mouse") ||
                    cBox_chooseComponent.getValue().equals("Graphics card") || cBox_chooseComponent.getValue().equals("Motherboard")) {

                try {
                        price = Double.parseDouble(price_Input.getText());
                    } catch (NumberFormatException e) {
                        canCreate = false;
                        label_Info.setText("Price requires a valid number");
                    }
                if(canCreate) {
                    comp = new PC_Component(componentType, description, price);
                    resetTextFields();
                }
            }


            //CREATE NEW PROCESSOR
            if(cBox_chooseComponent.getValue().equals("Processor")) {

                if(coreClock_Input.getText().isEmpty()) {
                    label_Info.setText("Core clock speed data is required!");
                    canCreate = false;
                } else {
                    try {
                        coreClock = Double.parseDouble(coreClock_Input.getText());
                        try {
                            ComponentValidator.checkCoreClock(coreClock);
                        } catch (InvalidCoreSpeedException e) {
                            canCreate = false;
                            label_Info.setText(e.getMessage());
                        }
                    } catch (NumberFormatException e) {
                        canCreate = false;
                        label_Info.setText("Core clock speed requires a valid number!");
                    }
                }

                try {
                    price = Double.parseDouble(price_Input.getText());
                } catch (NumberFormatException e) {
                    canCreate = false;
                    label_Info.setText("Price requires a valid number");
                }
                if(canCreate) {
                    PC_Component newComponent = new PC_Component(componentType, description, price);
                    comp = new Processor(newComponent, coreClock);
                    resetTextFields();
                } else {
                    comp = null;
                }
            }

            //CREATE NEW HARDDISK
            if(cBox_chooseComponent.getValue().equals("Harddisk")) {

                if(capacity_Input.getText().isEmpty()) {
                    label_Info.setText("Harddisk capacity data is required!");
                    canCreate = false;
                }else {
                    try {
                        capacity = Integer.parseInt(capacity_Input.getText());
                        try {
                            ComponentValidator.checkHdCapacity(capacity);
                        } catch (InvalidHDCapacityException e) {
                            canCreate = false;
                            label_Info.setText(e.getMessage());
                        }
                    } catch (NumberFormatException e) {
                        canCreate = false;
                        label_Info.setText("Harddisk capacity requires a valid whole number!");
                    }
                }

                try {
                    price = Double.parseDouble(price_Input.getText());
                } catch (NumberFormatException e) {
                    canCreate = false;
                    label_Info.setText("Price requires a valid number");
                }
                if(canCreate) {
                    PC_Component newComponent = new PC_Component(componentType, description, price);
                    comp = new Harddisk(newComponent, capacity);
                    resetTextFields();
                } else {
                    comp = null;
                }
            }

            //CREATE NEW Ram
            if(cBox_chooseComponent.getValue().equals("Memory")) {

                if(capacity_Input.getText().isEmpty()) {
                    label_Info.setText("Memory capacity data is required!");
                    canCreate = false;
                }else {
                    try {
                        capacity = Integer.parseInt(capacity_Input.getText());
                        try {
                            ComponentValidator.checkMemoryCapacity(capacity);
                        } catch (InvalidRamCapacityException e) {
                            canCreate = false;
                            label_Info.setText(e.getMessage());
                        }
                    } catch (NumberFormatException e) {
                        canCreate = false;
                        label_Info.setText("Memory capacity requires a valid whole number!");
                    }
                }
                try {
                    price = Double.parseDouble(price_Input.getText());
                } catch (NumberFormatException e) {
                    canCreate = false;
                    label_Info.setText("Price requires a valid number");
                }
                if(canCreate) {
                    PC_Component newComponent = new PC_Component(componentType, description, price);
                    comp = new Memory(newComponent, capacity);
                    resetTextFields();
                } else {
                    comp = null;
                }
            }

            //CREATE NEW MONITOR
            if(cBox_chooseComponent.getValue().equals("Monitor")) {
                if(screenSize_Input.getText().isEmpty()) {
                    label_Info.setText("Monitor screen size data is required!");
                    canCreate = false;
                } else  {
                    try {
                        screenSize = Double.parseDouble(screenSize_Input.getText());
                        try {
                            ComponentValidator.checkScreenSize(screenSize);
                        } catch (InvalidCoreSpeedException e) {
                            canCreate = false;
                            label_Info.setText(e.getMessage());
                        }
                    } catch (NumberFormatException e) {
                        canCreate = false;
                        label_Info.setText("Screen size requires a valid number!");
                    }
                }

                try {
                    price = Double.parseDouble(price_Input.getText());
                } catch (NumberFormatException e) {
                    canCreate = false;
                    label_Info.setText("Price requires a valid number");
                }
                if(canCreate) {
                    PC_Component newComponent = new PC_Component(componentType, description, price);
                    comp = new Monitor(newComponent, screenSize);
                    resetTextFields();
                } else  {
                    comp = null;
                }
            }
        }
         return comp;
    }

    //============================== EDITING DATA FROM TABLE VIEW ================================= //
    public void nameDataEdited(TableColumn.CellEditEvent<PC_Component, String> cellEditEvent) {
        try {
            if(cellEditEvent.getNewValue().length() == 0) {
                Dialogs.showErrorDialog("Enter a value!");
                cellEditEvent.getRowValue().setName(cellEditEvent.getOldValue());
                tableView_ComponentList.refresh();
            } else {
                cellEditEvent.getRowValue().setName(cellEditEvent.getNewValue());
            }

        } catch (Exception e) {
            cellEditEvent.getRowValue().setName(cellEditEvent.getOldValue());
            tableView_ComponentList.refresh();

        }
    }

    public void priceDataEdited(TableColumn.CellEditEvent<PC_Component, Double> cellEditEvent) {
        PC_Component c = cellEditEvent.getRowValue();
        if(cellEditEvent.getNewValue() == null) {
            c.setPrice(cellEditEvent.getOldValue());
            Dialogs.showErrorDialog("Enter a value!");
            tableView_ComponentList.refresh();
        } else {
            if(doubleStringConverter.isSuccessful()) {
                c.setPrice(cellEditEvent.getNewValue());
            } else {
                tableView_ComponentList.refresh();
            }
        }

    }

    public void capacityDataEdited(TableColumn.CellEditEvent<PC_Component, Integer> cellEditEvent) {
        if(cellEditEvent.getRowValue().getComponentType().equals("Harddisk") ||
                cellEditEvent.getRowValue().getComponentType().equals("Memory")) {
            if(integerStringConverter.isSuccessful()) {
                if(cellEditEvent.getRowValue().getComponentType().equals("Harddisk")) {
                    Harddisk hd = (Harddisk) cellEditEvent.getRowValue();
                    try {
                        if(cellEditEvent.getNewValue() == null) {
                            hd.setCapacity(cellEditEvent.getOldValue());
                            Dialogs.showErrorDialog("Enter a value!");
                            tableView_ComponentList.refresh();
                        } else {
                            hd.setCapacity(cellEditEvent.getNewValue());
                        }

                    } catch (InvalidHDCapacityException e) {
                        Dialogs.showErrorDialog(e.getMessage());
                        hd.setCapacity(cellEditEvent.getOldValue());
                        tableView_ComponentList.refresh();
                    }

                } else {
                    Memory memory = (Memory) cellEditEvent.getRowValue();
                    try {
                        if(cellEditEvent.getNewValue() == null) {
                            memory.setCapacity(cellEditEvent.getOldValue());
                            Dialogs.showErrorDialog("Enter a value!");
                            tableView_ComponentList.refresh();
                        } else {
                            memory.setCapacity(cellEditEvent.getNewValue());
                        }

                    } catch (InvalidRamCapacityException e) {
                        Dialogs.showErrorDialog(e.getMessage());
                        memory.setCapacity(cellEditEvent.getOldValue());
                        tableView_ComponentList.refresh();
                    }

                }
            } else {
                if(cellEditEvent.getRowValue().getComponentType().equals("Harddisk")) {
                    Harddisk hd = (Harddisk) cellEditEvent.getRowValue();
                    hd.setCapacity(cellEditEvent.getOldValue());
                    tableView_ComponentList.refresh();
                } else {
                    Memory memory = (Memory) cellEditEvent.getRowValue();
                    memory.setCapacity(cellEditEvent.getOldValue());
                    tableView_ComponentList.refresh();
                }
            }

        } else {
            Dialogs.dataNotRequired("Item does not require this data");
            tableView_ComponentList.refresh();
        }

    }

    public void coreClockDataEdited(TableColumn.CellEditEvent<PC_Component, Double> cellEditEvent) {
        if(cellEditEvent.getRowValue().getComponentType().equals("Processor")) {
            Processor p = (Processor) cellEditEvent.getRowValue();
            if(doubleStringConverter.isSuccessful()) {
                try {
                    if(cellEditEvent.getNewValue() == null) {
                        p.setCoreClock(cellEditEvent.getOldValue());
                        Dialogs.showErrorDialog("Enter a value!");
                        tableView_ComponentList.refresh();
                    } else {
                        p.setCoreClock(cellEditEvent.getNewValue());
                    }
                } catch (InvalidCoreSpeedException e) {
                    Dialogs.showErrorDialog(e.getMessage());
                    p.setCoreClock(cellEditEvent.getOldValue());
                    tableView_ComponentList.refresh();
                }

            } else {
                p.setCoreClock(cellEditEvent.getOldValue());
                tableView_ComponentList.refresh();
            }
        } else {
            Dialogs.dataNotRequired("Item does not require this data");
            tableView_ComponentList.refresh();
        }


    }



    public void screenSizeDataEdited(TableColumn.CellEditEvent<PC_Component, Double> cellEditEvent) {
        if(cellEditEvent.getRowValue().getComponentType().equals("Monitor")) {
            Monitor m = (Monitor) cellEditEvent.getRowValue();
            if(doubleStringConverter.isSuccessful()) {
                try {
                    if(cellEditEvent.getNewValue() == null) {
                        m.setScreenSize(cellEditEvent.getOldValue());
                        Dialogs.showErrorDialog("Enter a value!");
                        tableView_ComponentList.refresh();
                    } else {
                        m.setScreenSize(cellEditEvent.getNewValue());
                    }
                } catch (InvalidScreenSizeException e) {
                    Dialogs.showErrorDialog(e.getMessage());
                    m.setScreenSize(cellEditEvent.getOldValue());
                    tableView_ComponentList.refresh();
                }

            } else {
                m.setScreenSize(cellEditEvent.getOldValue());
                tableView_ComponentList.refresh();
            }
        } else {
            Dialogs.dataNotRequired("Item does not require this data");
            tableView_ComponentList.refresh();
        }
    }


    // =========================== FILTERING ITEMS ON TABLEVIEW ================================ //
    private void filter(String txt) {

        if(txt.equals("Processor")) {
            label_FilterInfo.setText("");
            List<PC_Component> filteredList = filterProcessors(cBox_PriceFilter.getValue());
            if(filteredList.isEmpty()) {
                tableView_ComponentList.getItems().clear();
                label_FilterInfo.setText("No Processors within the selected price range");
            }
            filteredList.forEach(i -> tableView_ComponentList.setItems((ObservableList<PC_Component>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("Harddisk")) {
            label_FilterInfo.setText("");
            List<PC_Component> filteredList = filterHarddisks(cBox_PriceFilter.getValue());
            if(filteredList.isEmpty()) {
                tableView_ComponentList.getItems().clear();
                label_FilterInfo.setText("No Harddisks within the selected price range");
            }
            filteredList.forEach(i -> tableView_ComponentList.setItems((ObservableList<PC_Component>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("Memory")) {
            label_FilterInfo.setText("");
            List<PC_Component> filteredList = filterMemory(cBox_PriceFilter.getValue());
            if(filteredList.isEmpty()) {
                tableView_ComponentList.getItems().clear();
                label_FilterInfo.setText("No Memory within the selected price range");
            }
            filteredList.forEach(i -> tableView_ComponentList.setItems((ObservableList<PC_Component>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("Monitor")) {
            label_FilterInfo.setText("");
            List<PC_Component> filteredList = filterMonitors(cBox_PriceFilter.getValue());
            if(filteredList.isEmpty()) {
                tableView_ComponentList.getItems().clear();
                label_FilterInfo.setText("No Monitors within the selected price range");
            }
            filteredList.forEach(i -> tableView_ComponentList.setItems((ObservableList<PC_Component>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("Graphics card")) {
            label_FilterInfo.setText("");
            List<PC_Component> filteredList = filterGraphicCard(cBox_PriceFilter.getValue());
            if(filteredList.isEmpty()) {
                tableView_ComponentList.getItems().clear();
                label_FilterInfo.setText("No Graphic cards within the selected price range");
            }
            filteredList.forEach(i -> tableView_ComponentList.setItems((ObservableList<PC_Component>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("Motherboard")) {
            label_FilterInfo.setText("");
            List<PC_Component> filteredList = filterMotherboards(cBox_PriceFilter.getValue());
            if(filteredList.isEmpty()) {
                tableView_ComponentList.getItems().clear();
                label_FilterInfo.setText("No Motherboards within the selected price range");
            }
            filteredList.forEach(i -> tableView_ComponentList.setItems((ObservableList<PC_Component>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("Keyboard")) {
            label_FilterInfo.setText("");
            List<PC_Component> filteredList = filterKeyboards(cBox_PriceFilter.getValue());
            if(filteredList.isEmpty()) {
                tableView_ComponentList.getItems().clear();
                label_FilterInfo.setText("No Keyboard within the selected price range");
            }
            filteredList.forEach(i -> tableView_ComponentList.setItems((ObservableList<PC_Component>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("Mouse")) {
            label_FilterInfo.setText("");
            List<PC_Component> filteredList = filterMouse(cBox_PriceFilter.getValue());
            if(filteredList.isEmpty()) {
                tableView_ComponentList.getItems().clear();
                label_FilterInfo.setText("No Mouse within the selected price range");
            }
            filteredList.forEach(i -> tableView_ComponentList.setItems((ObservableList<PC_Component>)filteredList));
            isListFiltered = true;
        }
        if(txt.equals("View all items")) {
            label_FilterInfo.setText("");
            List<PC_Component> filteredList = allItems(cBox_PriceFilter.getValue());
            if(filteredList.isEmpty()) {
                tableView_ComponentList.getItems().clear();
                label_FilterInfo.setText("No items within the selected price range");
            }
            filteredList.forEach(i -> tableView_ComponentList.setItems((ObservableList<PC_Component>)filteredList));
            isListFiltered = true;
        }


    }

    private List<PC_Component> filterProcessors(String price) {
        List<PC_Component> filteredList;
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

    private List<PC_Component> filterHarddisks(String price) {
        List<PC_Component> filteredList;
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


    private List<PC_Component> filterMemory(String price) {
        List<PC_Component> filteredList;
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


    private List<PC_Component> filterMonitors(String price) {
        List<PC_Component> filteredList;
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

    private List<PC_Component> filterGraphicCard(String price) {
        List<PC_Component> filteredList;
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

    private List<PC_Component> filterMotherboards(String price) {
        List<PC_Component> filteredList;
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

    private List<PC_Component> filterKeyboards(String price) {
        List<PC_Component> filteredList;
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

    private List<PC_Component> filterMouse(String price) {
        List<PC_Component> filteredList;
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
    private List<PC_Component> allItems(String price) {
        List<PC_Component> filteredList;
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

    // ====================== DISPLAY BRAND CHOICES ON CHOICEBOXES ============================= //
    private void changeBrandChoices() {
        String chosenComp = cBox_chooseComponent.getValue();
        String[] processorBrands = {"Intel", "AMD"};
        String[] gCards = {"Nvidia", "Asus", "MSI", "EVGA", "Gigabyte"};
        String[] mBoards = {"ASRock", "MSI", "Asus", "Gigabyte", "EVGA"};
        String[] hDbrands = {"Samsung", "Westerb Digital Blue", "Seagate", "Toshiba"};
        String[] memory = {"Corsair", "G.Skill", "Kingston", "HyperX"};
        String[] monitors = {"Asus", "BenQ", "Acer", "Samsung", "Dell", "LG"};
        String[] keyboards = {"Corsair", "Razer", "Logitech", "Steelseries", "HyperX", "Roccat"};
        String[] mice = {"Logitech", "Razer", "Corsair", "Steelseries"};
        if(chosenComp.equals("Processor")) {
            ObservableList<String> toRemove = cBox_Brand.getItems();
            cBox_Brand.getItems().removeAll(toRemove);
            cBox_Brand.getItems().setAll(processorBrands);
            cBox_Brand.setValue(processorBrands[0]);
            disableTextField(chosenComp);
        }
        if(chosenComp.equals("Graphics card")) {
            ObservableList<String> toRemove = cBox_Brand.getItems();
            cBox_Brand.getItems().removeAll(toRemove);
            cBox_Brand.getItems().setAll(gCards);
            disableTextField(chosenComp);
            cBox_Brand.setValue(gCards[0]);

        }
        if(chosenComp.equals("Motherboard")) {
            ObservableList<String> toRemove = cBox_Brand.getItems();
            cBox_Brand.getItems().removeAll(toRemove);
            cBox_Brand.getItems().setAll(mBoards);
            cBox_Brand.setValue(mBoards[0]);
            disableTextField(chosenComp);

        }
        if(chosenComp.equals("Harddisk")) {
            ObservableList<String> toRemove = cBox_Brand.getItems();
            cBox_Brand.getItems().removeAll(toRemove);
            cBox_Brand.getItems().setAll(hDbrands);
            cBox_Brand.setValue(hDbrands[0]);
            disableTextField(chosenComp);

        }
        if(chosenComp.equals("Memory")) {
            ObservableList<String> toRemove = cBox_Brand.getItems();
            cBox_Brand.getItems().removeAll(toRemove);
            cBox_Brand.getItems().setAll(memory);
            cBox_Brand.setValue(memory[0]);
            disableTextField(chosenComp);

        }
        if(chosenComp.equals("Monitor")) {
            ObservableList<String> toRemove = cBox_Brand.getItems();
            cBox_Brand.getItems().removeAll(toRemove);
            cBox_Brand.getItems().setAll(monitors);
            cBox_Brand.setValue(monitors[0]);
            disableTextField(chosenComp);

        }
        if(chosenComp.equals("Keyboard")) {
            ObservableList<String> toRemove = cBox_Brand.getItems();
            cBox_Brand.getItems().removeAll(toRemove);
            cBox_Brand.getItems().setAll(keyboards);
            cBox_Brand.setValue(keyboards[0]);
            disableTextField(chosenComp);

        }
        if(chosenComp.equals("Mouse")) {
            ObservableList<String> toRemove = cBox_Brand.getItems();
            cBox_Brand.getItems().removeAll(toRemove);
            cBox_Brand.getItems().setAll(mice);
            cBox_Brand.setValue(mice[0]);
            disableTextField(chosenComp);

        }
    }

    private void disableTextField(String value) {
        //description_Input,
        //price_Input, coreClock_Input, capacity_Input, screenSize_Input;

        if(value.equals("Processor")) {
            coreClock_Input.setVisible(true);
            capacity_Input.setVisible(false);
            screenSize_Input.setVisible(false);
        }
        if(value.equals("Harddisk") || value.equals("Memory")) {
            capacity_Input.setVisible(true);
            screenSize_Input.setVisible(false);
            coreClock_Input.setVisible(false);
        }
        if(value.equals("Monitor")) {
            capacity_Input.setVisible(false);
            screenSize_Input.setVisible(true);
            coreClock_Input.setVisible(false);
        }
        if(value.equals("Graphics card") || value.equals("Keyboard") || value.equals("Mouse") || value.equals("Motherboard"))  {
            capacity_Input.setVisible(false);
            screenSize_Input.setVisible(false);
            coreClock_Input.setVisible(false);
        }
    }

    private boolean checkEmptyFields() {
        ArrayList<TextField> textFields = new ArrayList<>();
        textFields.add(description_Input);
        textFields.add(price_Input);
        for(TextField txt : textFields) {
            if(txt.getText().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    private void resetTextFields() {
        // description_Input,
        //price_Input, coreClock_Input, capacity_Input, screenSize_Input;
        description_Input.setText("");
        price_Input.setText("");
        coreClock_Input.setText("");
        capacity_Input.setText("");
        screenSize_Input.setText("");
    }





    //============ LOAD THIS DATA IF sampleList.bin FILE FAILS TO LOAD =========== //
    private void loadSampleList() {
        PC_Component pc1 = new PC_Component("Processor", "AMD Ryzen 5 3600", 1837.58);
        Processor p1 = new Processor(pc1, 2.6);
        sampleList.add(p1);

        PC_Component pc2 = new PC_Component("Processor", "AMD Ryzen 7 3700X", 3018.66);
        Processor p2 = new Processor(pc2, 2.6);
        sampleList.add(p2);

        PC_Component pc3 = new PC_Component("Processor", "AMD Ryzen 9 3900X",4353.45);
        Processor p3 = new Processor(pc3, 3.8);
        sampleList.add(p3);

        PC_Component pc4 = new PC_Component("Processor", "Intel Core i5-9600K",3891.41);
        Processor p4 = new Processor(pc4, 3.7);
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
        Harddisk d2 = new Harddisk(pc10, 500);
        sampleList.add(d2);

        PC_Component pc11 = new PC_Component("Harddisk", "Western Digital Blue", 586.52);
        Harddisk d3 = new Harddisk(pc11, 2000);
        sampleList.add(d3);

        PC_Component pc12 = new PC_Component("Monitor", "Asus VG248QE ", 2561.96 );
        Monitor m1 = new Monitor(pc12, 24.0);
        sampleList.add(m1);

        PC_Component pc13 = new PC_Component("Monitor", "BenQ ZOWIE XL2411P", 2047.51 );
        Monitor m2 = new Monitor(pc13, 24.0);
        sampleList.add(m2);

        PC_Component pc14 = new PC_Component("Monitor", "Asus ROG SWIFT PG27UQ", 2047.51 );
        Monitor m3 = new Monitor(pc14, 27.0);
        sampleList.add(m3);

        PC_Component pc15 = new PC_Component("Monitor", "Acer GN246HL", 2097.22 );
        Monitor m4 = new Monitor(pc15, 24.0);
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
        Memory mry2 = new Memory(pc25, 16);
        sampleList.add(mry2);

        PC_Component pc26 = new PC_Component("Memory", "G.Skill Trident Z Neo", 1927.34);
        Memory mry3 = new Memory(pc26, 32);
        sampleList.add(mry3);

        PC_Component pc27 = new PC_Component("Memory", "Crucial Ballistix", 349.04);
        Memory mry4 = new Memory(pc27, 8);
        sampleList.add(mry4);

        PC_Component pc28 = new PC_Component("Motherboard", "ASRock AB350M", 2450.6);
        sampleList.add(pc28);

        PC_Component pc29 = new PC_Component("Motherboard", "Asus PRIME H310M-E R2.0", 574.0);
        sampleList.add(pc29);

        PC_Component pc30 = new PC_Component("Motherboard", "MSI Z170A GAMING M5", 4009.15);
        sampleList.add(pc30);

        PC_Component pc31 = new PC_Component("Motherboard", "Gigabyte GA-78LMT-USB3", 3352.34);
        sampleList.add(pc31);

        for(PC_Component pc : sampleList) {
            collection.add_Component(pc);
            componentList.add(pc);
        }


    }

}
