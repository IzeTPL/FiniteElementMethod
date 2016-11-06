package sample;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class Controller implements Initializable {

    @FXML //  fx:id="myButton"
    private Button myButton; // Value injected by FXMLLoader
    @FXML
    private Button solveButton;
    @FXML
    private ListView<String> elementList;
    @FXML
    private ListView<String> nodeList;
    @FXML
    private Label label;

    ObservableList<String> elements;
    ObservableList<String> nodes;

    //File
    private FileChooser fileChooser;
    private Stage stage;
    private File file;

    //MES
    private Grid grid;

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert myButton != null : "fx:id=\"myButton\" was not injected: check your FXML file 'simple.fxml'.";
        assert solveButton != null : "fx:id=\"solveButton\" was not injected: check your FXML file 'simple.fxml'.";
        assert elementList != null : "fx:id=\"elementList\" was not injected: check your FXML file 'simple.fxml'.";
        assert nodeList != null : "fx:id=\"nodeList\" was not injected: check your FXML file 'simple.fxml'.";
        assert label != null : "fx:id=\"label\" was not injected: check your FXML file 'simple.fxml'.";

        elements = elementList.getItems();
        nodes = nodeList.getItems();

        String str = Main.loader.getResource("data/test.json").toString();
        str = str.substring(6);
        file = new File(str);

        fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz dane wejściowe");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plik JSON", "*json"),
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*")
        );

        File initialDirectory = new File(System.getProperty("user.home"));

        fileChooser.setInitialDirectory(initialDirectory);

        myButton.setOnAction( (event) -> {

            file = fileChooser.showOpenDialog(stage);

        });

        solveButton.setOnAction( (event) -> {

            if(file != null) {
                grid = new Grid(file);
                fillLists();
                String text = "Temperature: " + grid.getEnvironmentTemperature() + "; Alpha: " + grid.getAlpha() + "; q: " + grid.getHeatFluxDensity();
                label.setText(text);
            }

        });


    }

    public void fillLists() {

        for(int i = 0; i < grid.getElementQuantity(); i++) {

            String string =
                    "Nodes: " + grid.getElements()[i].getFirstID() + ", " + grid.getElements()[i].getSecondID() + "; " +
                    "Area: " + grid.getElements()[i].getArea() + "; " +
                            "Length: " + grid.getElements()[i].getLength() + "; " +
                    "k: " + grid.getElements()[i].getkValue();


            elements.add(string);

        }

        for(int i = 0; i < grid.getNodeQuantity(); i++) {

            String string = "ID: " + i + "; Positon: " + grid.getNodes()[i].getPositionX();

            if(grid.getNodes()[i].getBoundaryConditions() == BoundaryConditions.HEAT_FLUX_DENSITY) string += "; BC - q";
            if(grid.getNodes()[i].getBoundaryConditions() == BoundaryConditions.CONVECTION) string += "; BC - c";

            nodes.add(string);

        }

    }

    public File getFile() {
        return file;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
