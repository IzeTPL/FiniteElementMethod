package fem.statystate;

import fem.BoundaryConditions;
import fem.Element;
import fem.Node;
import model.IGrid;
import org.apache.commons.math3.linear.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

/**
 * Created by marian on 30.10.16.
 */

public class StatyStateGrid implements IGrid{

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

    public StatyStateGrid(File file) {

        this.file = file;

        parser = new JSONParser();

        generateGrid();

    }

    public void generateGrid() {

        try{

            Object object = parser.parse(new FileReader(file));
            jsonFileObject = (JSONObject) object;

            readElements();
            readBoundaryConditions();
            readParameters();

            init();

            generateLocalCoefficientMatrix();
            generateGlobalCoefficientMatrix();
            solve();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void init() {

        temperatures = new double[nodeQuantity];
        globalCoefficientMatrix = new double[nodeQuantity][nodeQuantity];
        globalVector = new double[nodeQuantity];

        Arrays.fill(temperatures, 0.0);
        Arrays.fill(globalVector, 0.0);

        for(double[] row: globalCoefficientMatrix) {

            Arrays.fill(row, 0.0);

        }

    }

    public void readElements() {

        JSONArray jsonArray = (JSONArray) jsonFileObject.get("DataSets");

        for(int i = 0; i < jsonArray.size(); i++) {

            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            elementQuantity += ( (Number) jsonObject.get("elementsQuantity") ).intValue();

        }


        elements = new Element[elementQuantity];
        nodeQuantity = elementQuantity + 1;
        nodes = new Node[nodeQuantity];

        for (int i = 0; i < elementQuantity + 1; i++) {
            nodes[i] = new Node();
        }

        int count = 0;

        for(int i = 0; i < jsonArray.size(); i++) {

            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            int elementQuantity = ( (Number) jsonObject.get("elementsQuantity") ).intValue();
            int stop = count;

            //int startID =( (Number) jsonObject.get("StartID") ).intValue();

            for(int j = count; j < stop + elementQuantity; j++) {

                elements[j] = new Element();
                elements[j].setArea( ( (Number) jsonObject.get("area") ).doubleValue() );
                elements[j].setkValue( ( (Number) jsonObject.get("kValue") ).doubleValue() );
                elements[j].setLength( ( (Number) jsonObject.get("length") ).doubleValue() );
                elements[j].setFirstID(j);
                elements[j].setSecondID(j+1);

                count++;

            }

        }

    }

    public void readBoundaryConditions() {

        JSONArray jsonArray = (JSONArray) jsonFileObject.get("BoundaryConditions");

        for(int i = 0; i < jsonArray.size(); i++) {

            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            int ID = ((Number) jsonObject.get("NodeID")).intValue();
            int BC = ((Number) jsonObject.get("BoundaryConditionID")).intValue();
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

        for (int i = 0; i < elements.length; i++) {

            double coefficient = (elements[i].getkValue() * elements[i].getArea()) / elements[i].getLength();

            double hMatrix[][] = new double[2][2];
            double pMatrix[] = new double[2];

            Arrays.fill(pMatrix, 0.0);

            for (double[] row: hMatrix) {
                Arrays.fill(row, 0.0);
            }

            hMatrix[0][0] = coefficient;
            hMatrix[0][1] = -coefficient;
            hMatrix[1][0] = -coefficient;
            hMatrix[1][1] = coefficient;

            if(nodes[elements[i].getFirstID()].getBoundaryConditions() == BoundaryConditions.CONVECTION) {
                hMatrix[0][0] += (elements[i].getArea() * alpha);
                pMatrix[0] = -(alpha  * elements[i].getArea() * environmentTemperature);
            }

            if(nodes[elements[i].getSecondID()].getBoundaryConditions() == BoundaryConditions.CONVECTION) {
                hMatrix[1][1] += (elements[i].getArea() * alpha);
                pMatrix[1] = -(alpha * elements[i].getArea() * environmentTemperature);
            }

            if(nodes[elements[i].getFirstID()].getBoundaryConditions() == BoundaryConditions.HEAT_FLUX_DENSITY)
                pMatrix[0] = heatFluxDensity*elements[i].getArea();

            if(nodes[elements[i].getSecondID()].getBoundaryConditions() == BoundaryConditions.HEAT_FLUX_DENSITY)
                pMatrix[1] = heatFluxDensity*elements[i].getArea();


            elements[i].setLocalHMatrix(hMatrix);
            elements[i].setLocalPMatrix(pMatrix);

        }

    }

    @Override
    public void generateLocalVector() {

    }

    public void generateGlobalCoefficientMatrix() {

        for (int i = 0; i < elements.length; i++) {

            int x = elements[i].getFirstID();
            int y = elements[i].getSecondID();

            globalCoefficientMatrix[x][x] += elements[i].getLocalHMatrix()[0][0];
            globalCoefficientMatrix[x][y] += elements[i].getLocalHMatrix()[0][1];
            globalCoefficientMatrix[y][x] += elements[i].getLocalHMatrix()[1][0];
            globalCoefficientMatrix[y][y] += elements[i].getLocalHMatrix()[1][1];

            globalVector[x] += elements[i].getLocalPMatrix()[0];
            globalVector[y] += elements[i].getLocalPMatrix()[1];

        }

    }

    @Override
    public void generateGlobalVector() {

    }

    public void solve() {

        RealMatrix coefficients = new Array2DRowRealMatrix(globalCoefficientMatrix, false);
        DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
        RealVector constatnts = new ArrayRealVector(globalVector, false);
        RealVector solution = solver.solve(constatnts);

        for (int i = 0; i < nodeQuantity; i++) {

            temperatures[i] = -solution.getEntry(i);
            nodes[i].setTemperature(temperatures[i]);

        }

    }

    public void save() {



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

    public double[][] getGlobalCoefficientMatrix() {
        return globalCoefficientMatrix;
    }

    public double[] getGlobalVector() {
        return globalVector;
    }
}
