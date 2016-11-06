package sample;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class Controller implements Initializable {

    @FXML
    private GridPane gridPaneMatrix;
    @FXML //  fx:id="myButton"
    private Button myButton; // Value injected by FXMLLoader
    @FXML
    private Button solveButton;
    @FXML
    private Button saveButton;
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

        String str = Main.loader.getResource("data/zadanie.json").toString();
        str = str.substring(6);
        file = new File(str);

        fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz dane wejściowe");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plik JSON", "*json"),
                new FileChooser.ExtensionFilter("Plik tekstowy", "*txt"),
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*")
        );

        File initialDirectory = new File(System.getProperty("user.home"));

        fileChooser.setInitialDirectory(initialDirectory);

        myButton.setOnAction( (event) -> file = fileChooser.showOpenDialog(stage));

        solveButton.setOnAction( (event) -> {

            if(file != null) {
                grid = new Grid(file);
                fillLists();
                String text = "Temperature: " + grid.getEnvironmentTemperature() + "; Alpha: " + grid.getAlpha() + "; q: " + grid.getHeatFluxDensity();
                label.setText(text);
                showMatrix();
            }

        });

        saveButton.setOnAction( (event) -> {

            file = fileChooser.showSaveDialog(stage);
            if(file != null){
                SaveFile(file);
            }

        });

    }

    private void SaveFile(File file){

        String content = "";

        for (int i = 0; i < grid.getNodes().length; i++) {

            content += "Node ID: " + i + "; x: " + grid.getNodes()[i].getPositionX() +
                    "; T: " + grid.getNodes()[i].getTemperature() + "\n";

        }

        try {
            FileWriter fileWriter = null;

            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void fillLists() {

        for(int i = 0; i < grid.getElementQuantity(); i++) {

            String string =
                    "Nodes: " + grid.getElements()[i].getFirstID() + ", " + grid.getElements()[i].getSecondID() + "; " +
                    "; Area: " + grid.getElements()[i].getArea() +
                    "; Length: " + grid.getElements()[i].getLength() +
                    "; k: " + grid.getElements()[i].getkValue();


            elements.add(string);

        }

        for(int i = 0; i < grid.getNodeQuantity(); i++) {

            String string = "ID: " + i + "; Positon: " + grid.getNodes()[i].getPositionX() + "; T: " + grid.getNodes()[i].getTemperature();

            if(grid.getNodes()[i].getBoundaryConditions() == BoundaryConditions.HEAT_FLUX_DENSITY) string += "; BC - q";
            if(grid.getNodes()[i].getBoundaryConditions() == BoundaryConditions.CONVECTION) string += "; BC - c";

            nodes.add(string);

        }

    }

    public void showMatrix() {

        Label[][] matrix; //names the grid of buttons

        gridPaneMatrix.getChildren().clear();

        matrix = new Label[grid.getNodeQuantity()][grid.getNodeQuantity() + 1];
        for(int x = 0; x < grid.getNodeQuantity(); x++)
        {
            for(int y = 0; y < grid.getNodeQuantity(); y++)
            {

                matrix[x][y] = new Label(/*"(" + rand1 + ")"*/);
                matrix[x][y].setMinSize(75, 75);
                matrix[x][y].setAlignment(Pos.CENTER);
                matrix[x][y].setStyle("-fx-border-color: black;");
                matrix[x][y].setText(String.valueOf(grid.getGlobalCoefficientMatrix()[x][y]));

                GridPane.setColumnIndex(matrix[x][y], y);
                GridPane.setRowIndex(matrix[x][y], x);
                gridPaneMatrix.getChildren().add(matrix[x][y]);
            }

            matrix[x][grid.getNodeQuantity()] = new Label(/*"(" + rand1 + ")"*/);
            matrix[x][grid.getNodeQuantity()].setMinSize(75, 75);
            matrix[x][grid.getNodeQuantity()].setAlignment(Pos.CENTER);
            matrix[x][grid.getNodeQuantity()].setStyle("-fx-border-color: black;");
            matrix[x][grid.getNodeQuantity()].setText(String.valueOf(grid.getGlobalVector()[x]));

            GridPane.setColumnIndex(matrix[x][grid.getNodeQuantity()], grid.getNodeQuantity());
            GridPane.setRowIndex(matrix[x][grid.getNodeQuantity()], x);
            gridPaneMatrix.getChildren().add(matrix[x][grid.getNodeQuantity()]);

        }

    }

    public File getFile() {
        return file;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
