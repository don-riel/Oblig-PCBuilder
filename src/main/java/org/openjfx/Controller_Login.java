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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller_Login implements Initializable {

    @FXML
    private TextField input_AdminBrukerNavn;

    @FXML
    private TextField input_AdminPass;

    @FXML
    private Button btn_LogIn, btn_Cancel;

    @FXML
    private Label label_LoggInnError;

    @FXML
    private AnchorPane anch_Pane;

    private ThreadEx threadEx;

    String adminBrukerNavn = "admin";
    String adminPass = "123";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        anch_Pane.setStyle("-fx-background-color:linear-gradient(to right bottom, #126374, #2f7781, #4a8c8d, #65a099, #81b5a5);");
    }

    @FXML
    void loggInn_Admin(ActionEvent event) {
        threadEx = new ThreadEx();
        threadEx.setOnSucceeded(this::threadDone);
        threadEx.setOnFailed(this::threadFailed);
        Thread th = new Thread(threadEx);
        th.setDaemon(true);
        input_AdminBrukerNavn.setDisable(true);
        input_AdminPass.setDisable(true);
        btn_LogIn.setDisable(true);
        btn_Cancel.setDisable(true);
        label_LoggInnError.setText("Signing in...");
        th.start();
    }

    private void threadDone(WorkerStateEvent event) {
        btn_LogIn.setDisable(false);
        btn_Cancel.setDisable(false);
        input_AdminBrukerNavn.setDisable(false);
        input_AdminPass.setDisable(false);
        label_LoggInnError.setText("");
        if(input_AdminBrukerNavn.getText().isEmpty() || input_AdminPass.getText().isEmpty()) {
            label_LoggInnError.setText("Username and password required!");
        } else {
            if(input_AdminBrukerNavn.getText().equals(adminBrukerNavn) && input_AdminPass.getText().equals(adminPass)) {
                FXMLLoader load = new FXMLLoader();
                load.setLocation(getClass().getResource("admin_Page.fxml"));
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

                //LOGIN WINDOW CLOSES WHEN LOGIN IS SUCCESSFUL
                Stage currentStage = (Stage) btn_Cancel.getScene().getWindow();
                currentStage.close();
            } else {
                label_LoggInnError.setText("Wrong username or password!");
            }
        }

    }

    private void threadFailed(WorkerStateEvent event) {
        org.openjfx.dialogs.Dialogs.showErrorDialog("Something went wrong");
    }



    public void cancel_Login(ActionEvent event) {
        Stage stage = (Stage) btn_Cancel.getScene().getWindow();
        stage.close();
    }


}
