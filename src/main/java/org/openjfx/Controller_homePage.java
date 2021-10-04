package org.openjfx;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class Controller_homePage implements Initializable {

    @FXML
    private Button btn_SuperBruker;

    @FXML
    private Button btn_SluttBruker;

    @FXML
    private Label label_Info;

    @FXML
    VBox vBox;


    private ThreadEx threadEx;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBox.setStyle("-fx-background-color:linear-gradient(to right bottom, #126374, #2f7781, #4a8c8d, #65a099, #81b5a5);");
    }

    @FXML
    void login_SluttBruker(ActionEvent event) {
        FXMLLoader load = new FXMLLoader();
        load.setLocation(getClass().getResource("user_Page.fxml"));
        try {
            load.load();
        }
        catch (Exception e) {
            System.out.println("something went wrong");
        }
        Parent p = load.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(p));

        stage.show();

    }

    @FXML
    void login_SuperBruker(ActionEvent event) {
        FXMLLoader load = new FXMLLoader();
        load.setLocation(getClass().getResource("login.fxml"));
        try {
            load.load();
        }
        catch (Exception e) {
            System.out.println("something went wrong");
        }
        Parent p = load.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(p));

        stage.show();

    }





}
