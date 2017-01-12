package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Marian on 12.01.2017.
 */
public class MainController implements Initializable{

    private Stage stage;
    @FXML
    private Tab statyState;
    @FXML
    private Tab transientSolution;
    @FXML
    private StatyStateController statyStateController;
    @FXML
    private TransientSolutionController transientSolutionController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        statyStateController.setStage(stage);
        transientSolutionController.setStage(stage);


    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
