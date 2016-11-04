package sample;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;


public class Controller implements Initializable {

    @FXML //  fx:id="myButton"
    private Button myButton; // Value injected by FXMLLoader
    private FileChooser fileChooser;


    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert myButton != null : "fx:id=\"myButton\" was not injected: check your FXML file 'simple.fxml'.";

        fileChooser = new FileChooser();

        myButton.setOnAction((event) -> {

            public void handle(final ActionEvent e) {
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    openFile(file);
                }
            }

        });


    }

}
