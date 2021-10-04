package org.openjfx.dialogs;

import javafx.scene.control.Alert;

public class Dialogs {
    public static void showErrorDialog(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Invalid input!");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void showFileErrorDialog(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("File Error!");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void showSuccessDialog(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Person Register");
        alert.setHeaderText("Operation successful");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void missingField(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Fields required missing!");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void fileNotFoundDialog(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid input");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void dataNotRequired(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Data not required");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void cannontAddComponent(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Component!");
        alert.setHeaderText("Component already exists!");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void showMissingComponents(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Component!");
        alert.setHeaderText("Build missing components!");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void listIsEmpty(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("List is empty!");
        alert.setContentText(msg);
        alert.showAndWait();
    }



}
