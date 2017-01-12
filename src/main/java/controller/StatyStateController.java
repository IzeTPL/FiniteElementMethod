package controller;

import fem.BoundaryConditions;
import fem.statystate.StatyStateGrid;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class StatyStateController implements Initializable {

    @FXML
    private Button browseButton1;
    @FXML
    private Button solveButton1;
    @FXML
    private Button saveButton1;
    @FXML
    private Label label1;
    @FXML
    private ListView<String> elementList1;
    @FXML
    private ListView<String> nodeList1;
    @FXML
    private GridPane gridPaneMatrix1;
    @FXML
    private HBox hBox1;

    ObservableList<String> elements1;
    ObservableList<String> nodes1;

    //File
    private FileChooser fileChooser;
    private Stage stage;
    private File file;

    //MES
    private StatyStateGrid grid;

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        elements1 = elementList1.getItems();
        nodes1 = nodeList1.getItems();

        file = loadFile("data/1/zadanie2.json");

        fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz dane wejściowe");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plik JSON", "*json"),
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*")
        );

        File initialDirectory = new File(System.getProperty("user.home"));

        fileChooser.setInitialDirectory(initialDirectory);

        browseButton1.setOnAction( (event) -> file = fileChooser.showOpenDialog(stage));

        solveButton1.setOnAction( (event) -> {

            if(file != null) {
                grid = new StatyStateGrid(file);
                fillLists();
                String text = "Environment temperature: " + grid.getEnvironmentTemperature() + "; Alpha: " + grid.getAlpha() + "; q: " + grid.getHeatFluxDensity();
                label1.setText(text);
                showMatrix();
            }

        });

        saveButton1.setOnAction( (event) -> {

            file = fileChooser.showSaveDialog(stage);
            if(file != null){
                SaveFile(file);
            }

        });

    }

    private void SaveFile(File file){

        String content = "";

        for (int i = 0; i < grid.getNodes().length; i++) {

            content += "Node ID: " + i +
                    "; T: " + grid.getNodes()[i].getTemperature() + System.lineSeparator();

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

        nodes1.clear();
        elements1.clear();

        for(int i = 0; i < grid.getElementQuantity(); i++) {

            String string =
                    "Nodes: " + grid.getElements()[i].getFirstID() + ", " + grid.getElements()[i].getSecondID() +
                    "; Area: " + grid.getElements()[i].getArea() +
                    "; Length: " + grid.getElements()[i].getLength() +
                    "; k: " + grid.getElements()[i].getkValue();


            elements1.add(string);

        }

        for(int i = 0; i < grid.getNodeQuantity(); i++) {

            double temp = grid.getNodes()[i].getTemperature();
            String string = "ID: " + i + "; T: " + temp;

            if(grid.getNodes()[i].getBoundaryConditions() == BoundaryConditions.HEAT_FLUX_DENSITY) string += "; BC - q";
            if(grid.getNodes()[i].getBoundaryConditions() == BoundaryConditions.CONVECTION) string += "; BC - c";

            nodes1.add(string);

        }

        nodeList1.prefWidth(hBox1.getWidth()/2);

    }

    public void showMatrix() {

        Label[][] matrix; //names the grid of buttons

        gridPaneMatrix1.getChildren().clear();

        matrix = new Label[grid.getNodeQuantity()][grid.getNodeQuantity() + 1];
        for(int x = 0; x < grid.getNodeQuantity(); x++)
        {
            for(int y = 0; y < grid.getNodeQuantity(); y++)
            {

                matrix[x][y] = new Label(/*"(" + rand1 + ")"*/);
                matrix[x][y].setMinSize(75, 75);
                matrix[x][y].setAlignment(Pos.CENTER);
                matrix[x][y].setStyle("-fx-border-color: black;");
                matrix[x][y].setText(String.format("%.2f", grid.getGlobalCoefficientMatrix()[x][y]));

                GridPane.setColumnIndex(matrix[x][y], y);
                GridPane.setRowIndex(matrix[x][y], x);
                gridPaneMatrix1.getChildren().add(matrix[x][y]);
            }

            matrix[x][grid.getNodeQuantity()] = new Label(/*"(" + rand1 + ")"*/);
            matrix[x][grid.getNodeQuantity()].setMinSize(75, 75);
            matrix[x][grid.getNodeQuantity()].setAlignment(Pos.CENTER);
            matrix[x][grid.getNodeQuantity()].setStyle("-fx-border-color: black;");
            matrix[x][grid.getNodeQuantity()].setText(String.format("%.2f", grid.getGlobalVector()[x]));

            GridPane.setColumnIndex(matrix[x][grid.getNodeQuantity()], grid.getNodeQuantity());
            GridPane.setRowIndex(matrix[x][grid.getNodeQuantity()], x);
            gridPaneMatrix1.getChildren().add(matrix[x][grid.getNodeQuantity()]);

        }

    }

    public File loadFile(String fileName) {
        return new File(StatyStateController.class.getClassLoader().getResource(fileName).toString().substring(6));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
