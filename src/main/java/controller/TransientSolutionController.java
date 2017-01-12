package controller;

import fem.BoundaryConditions;
import fem.transientsolution.TransientSolutionGrid;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Marian on 12.01.2017.
 */
public class TransientSolutionController implements Initializable{

    @FXML
    private Button browseButton2;
    @FXML
    private Button solveButton2;
    @FXML
    private Button saveButton2;
    @FXML
    private Label label2;
    @FXML
    private ListView<String> tauList2;
    @FXML
    private ListView<String> elementList2;
    @FXML
    private ListView<String> nodeList2;
    @FXML
    private GridPane gridPaneMatrix2;
    @FXML
    private HBox hBox2;
    @FXML
    private LineChart<Double, Double> lineChart;

    private XYChart.Series<Double, Double> series1;
    private XYChart.Series<Double, Double> series2;

    ObservableList<String> tau2;
    ObservableList<String> elements2;
    ObservableList<String> nodes2;

    //File
    private FileChooser fileChooser;
    private Stage stage;
    private File file;

    //MES
    private TransientSolutionGrid grid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        lineChart.setCreateSymbols(false);
        elements2 = elementList2.getItems();
        nodes2 = nodeList2.getItems();
        tau2 = tauList2.getItems();

        series1 = new XYChart.Series<>();
        series2 = new XYChart.Series<>();

        lineChart.getData().add(series1);
        lineChart.getData().add(series2);

        file = loadFile("data/2/zadanie.json");

        fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz dane wejściowe");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plik JSON", "*json"),
                new FileChooser.ExtensionFilter("Plik tekstowy", "*txt"),
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*")
        );

        File initialDirectory = new File(System.getProperty("user.home"));

        fileChooser.setInitialDirectory(initialDirectory);

        browseButton2.setOnAction(event -> file = fileChooser.showOpenDialog(stage));

        solveButton2.setOnAction(event -> {

            if(file != null) {
                grid = new TransientSolutionGrid(file);
                fillLists(0);
                drawChart();
                String text = "Starting temperature: " + grid.getStartingTemperature() + "; Environment temperature: " + grid.getEnvironmentTemperature() + "; Alpha: " + grid.getAlpha();
                label2.setText(text);
                showMatrix();
            }

        });

        saveButton2.setOnAction(event -> {

            file = fileChooser.showSaveDialog(stage);
            if(file != null){
                SaveFile(file);
            }

        });

        tauList2.setOnMouseClicked(event -> {
            fillLists(tauList2.getSelectionModel().getSelectedIndex());
        });

    }

    private void SaveFile(File file){

        String content = "";

        for (int i = 0; i < grid.getIndex(); i++) {

            content += System.lineSeparator() + "Time: " + grid.getTau().get(i) + System.lineSeparator();
            for (int j = 0; j < grid.getElementQuantity(); j++) {
                content += "T: " + grid.getTemperatures().get(i)[j] + System.lineSeparator();
            }


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

    public void fillLists(int index) {

        tau2.clear();
        nodes2.clear();
        elements2.clear();

        for(int i = 0; i < grid.getIndex(); i++) {

            String string = "" + grid.getTau().get(i);
            tau2.add(string);

        }

        for(int i = 0; i < grid.getElementQuantity(); i++) {

            String string =
                    "Nodes: " + grid.getElements()[i].getFirstID() + ", " + grid.getElements()[i].getSecondID() +
                            "; k: " + grid.getElements()[i].getkValue();


            elements2.add(string);

        }

        for(int i = 0; i < grid.getNodeQuantity(); i++) {

            double temp = grid.getNodes()[i].getTemperature();
            String string = "ID: " + i + "; T: " + grid.getTemperatures().get(index)[i];

            if(grid.getNodes()[i].getBoundaryConditions() == BoundaryConditions.HEAT_FLUX_DENSITY) string += "; BC - q";
            if(grid.getNodes()[i].getBoundaryConditions() == BoundaryConditions.CONVECTION) string += "; BC - c";

            nodes2.add(string);

        }

        nodeList2.prefWidth(hBox2.getWidth()/2);

    }

    public void showMatrix() {

        Label[][] matrix; //names the grid of buttons

        gridPaneMatrix2.getChildren().clear();

        matrix = new Label[grid.getNodeQuantity()][grid.getNodeQuantity()];
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
                gridPaneMatrix2.getChildren().add(matrix[x][y]);
            }

        }

    }

    public void drawChart() {

        series1.getData().clear();
        series2.getData().clear();

        series1.getData().add(new XYChart.Data<>(0d, grid.getStartingTemperature()));
        series2.getData().add(new XYChart.Data<>(0d, grid.getStartingTemperature()));

        for (int i = 0; i < grid.getIndex(); i++) {
            series1.getData().add(new XYChart.Data<>(grid.getTau().get(i), grid.getTemperatures().get(i)[0]));
            series2.getData().add(new XYChart.Data<>(grid.getTau().get(i), grid.getTemperatures().get(i)[grid.getElementQuantity()]));
        }

    }

    public File loadFile(String fileName) {
        return new File(StatyStateController.class.getClassLoader().getResource(fileName).toString().substring(6));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
