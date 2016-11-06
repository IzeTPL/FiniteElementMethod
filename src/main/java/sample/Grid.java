package sample;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;

/**
 * Created by marian on 30.10.16.
 */

public class Grid {

    private int nodeQuantity;
    private int elementQuantity;
    private Element[] elements;
    private Node[] nodes;
    private double heatFluxDensity;
    private double alpha;
    private double environmentTemperature;
    private double globalCoefficientMatrix[][];
    private double globalVector[];
    private double temperatures[];

    private JSONParser parser;
    private JSONObject jsonFileObject;
    private File file;

    public Grid(File file) {

        this.file = file;

        parser = new JSONParser();

        generateGrid();

    }

    public void generateGrid() {

        try{

            Object object = parser.parse(new FileReader(file));
            jsonFileObject = (JSONObject) object;

            readElements();
            readNodes();
            readBoundaryConditions();
            readParameters();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void readElements() {

        JSONArray jsonArray = (JSONArray) jsonFileObject.get("DataSets");

        for(int i = 0; i < jsonArray.size(); i++) {

            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            elementQuantity += ( (Number) jsonObject.get("elementsQuantity") ).intValue();

        }


        elements = new Element[elementQuantity];

        int count = 0;

        for(int i = 0; i < jsonArray.size(); i++) {

            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            int elementQuantity = ( (Number) jsonObject.get("elementsQuantity") ).intValue();
            int stop = count;

            int startID =( (Number) jsonObject.get("StartID") ).intValue();

            for(int j = count; j < stop + elementQuantity; j++) {

                elements[j] = new Element();
                elements[j].setArea( ( (Number) jsonObject.get("area") ).doubleValue() );
                elements[j].setkValue( ( (Number) jsonObject.get("kValue") ).doubleValue() );
                elements[j].setFirstID(startID);
                elements[j].setSecondID(++startID);

                count++;

            }

        }

    }

    public void readNodes() {

        JSONArray jsonArray = (JSONArray) jsonFileObject.get("NodesSet");

        nodeQuantity = elementQuantity + 1;
        nodes = new Node[nodeQuantity];

        for(int i = 0; i < jsonArray.size(); i++) {

            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            int ID =  ( (Number) jsonObject.get("NodeID") ).intValue();

            nodes[ID] = new Node();
            nodes[ID].setPositionX( ( (Number) jsonObject.get("positionX") ).doubleValue() );

        }

    }

    public void readBoundaryConditions() {

        JSONArray jsonArray = (JSONArray) jsonFileObject.get("BoundaryConditions");

        for(int i = 0; i < jsonArray.size(); i++) {

            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            int ID = ((Number) jsonObject.get("NodeID")).intValue();
            int BC = ((Number) jsonObject.get("BoundaryConditionID")).byteValue();
            if(BC == 1) nodes[ID].setBoundaryConditions(BoundaryConditions.HEAT_FLUX_DENSITY);
            if(BC == 2) nodes[ID].setBoundaryConditions(BoundaryConditions.CONVECTION);

        }


    }

    public void readParameters() {

        environmentTemperature = ( (Number) jsonFileObject.get("environmentTemperature") ).doubleValue();
        alpha = ( (Number) jsonFileObject.get("alpha") ).doubleValue();
        heatFluxDensity = ( (Number) jsonFileObject.get("heatFluxDensity") ).doubleValue();

    }

    public void generateLocalCoefficientMatrix() {



    }

    public void generateGlobalCoefficientMatrix() {



    }

    public int getNodeQuantity() {
        return nodeQuantity;
    }

    public int getElementQuantity() {
        return elementQuantity;
    }

    public Element[] getElements() {
        return elements;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public double getHeatFluxDensity() {
        return heatFluxDensity;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getEnvironmentTemperature() {
        return environmentTemperature;
    }
}
