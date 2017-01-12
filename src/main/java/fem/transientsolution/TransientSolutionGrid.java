package fem.transientsolution;

import fem.BoundaryConditions;
import fem.Element;
import fem.Node;
import fem.gaussianintegral.TwoPointsGaussianIntegral;
import model.IGrid;
import org.apache.commons.math3.linear.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by marian on 30.10.16.
 */

public class TransientSolutionGrid implements IGrid {

    private int nodeQuantity;
    private int elementQuantity;
    private int index;

    private Element[] elements;
    private Node[] nodes;
    private double alpha;
    private double environmentTemperature;
    private double globalCoefficientMatrix[][];
    private double globalVector[];
    private ArrayList<Double[]> temperatures;
    private double shapeFunctions[][];

    private double deltaRadius;
    private double materialDensity;
    private double deltaTau;
    private ArrayList<Double> tau;
    private double maxTau;
    private double startingTemperature;
    private double maxRadius;
    private double heatCapacity;
    private double kValue;

    private JSONParser parser;
    private JSONObject jsonFileObject;
    private File file;

    public TransientSolutionGrid(File file) {

        this.file = file;

        parser = new JSONParser();

        generateGrid();

    }

    public void generateGrid() {

        try {

            Object object = parser.parse(new FileReader(file));
            jsonFileObject = (JSONObject) object;

            shapeFunctions = new double[2][2];

            readParameters();
            readElements();
            readBoundaryConditions();

            init();
            generateLocalCoefficientMatrix();
            generateGlobalCoefficientMatrix();

            for (double tau = deltaTau; tau <= maxTau; tau += deltaTau) {

                this.tau.add(tau);
                generateLocalVector();
                generateGlobalVector();
                solve();
                index++;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void init() {

        tau = new ArrayList<>();
        temperatures = new ArrayList<>();
        globalCoefficientMatrix = new double[nodeQuantity][nodeQuantity];
        globalVector = new double[nodeQuantity];

        Arrays.fill(globalVector, 0.0);

        for (double[] row : globalCoefficientMatrix) {

            Arrays.fill(row, 0.0);

        }

    }

    public void readElements() {

        double radius = 0.0;
        elementQuantity = 0;

        while (radius < maxRadius) {

            elementQuantity++;
            radius += deltaRadius;

        }

        elements = new Element[elementQuantity];
        nodeQuantity = elementQuantity + 1;
        nodes = new Node[nodeQuantity];

        for (int i = 0; i < elementQuantity + 1; i++) {
            nodes[i] = new Node();
        }

        for (int j = 0; j < elementQuantity; j++) {


            elements[j] = new Element();
            elements[j].setkValue(kValue);
            elements[j].setFirstID(j);
            elements[j].setSecondID(j + 1);


        }

        double x = 0.0;

        for (int i = 0; i < nodeQuantity; i++) {

            nodes[i].setPositionX(x);
            nodes[i].setTemperature(startingTemperature);
            x += deltaRadius;

        }

    }

    public void readBoundaryConditions() {

        for (int i = 0; i < elementQuantity; i++) {

            nodes[i].setBoundaryConditions(BoundaryConditions.NULL);

        }

        nodes[elementQuantity].setBoundaryConditions(BoundaryConditions.CONVECTION);

    }

    public void readParameters() {

        environmentTemperature = ((Number) jsonFileObject.get("environmentTemperature")).doubleValue();
        alpha = ((Number) jsonFileObject.get("alpha")).doubleValue();
        materialDensity = ((Number) jsonFileObject.get("materialDensity")).doubleValue();
        startingTemperature = ((Number) jsonFileObject.get("startingTemperature")).doubleValue();
        maxRadius = ((Number) jsonFileObject.get("maxRadius")).doubleValue();
        deltaRadius = ((Number) jsonFileObject.get("deltaRadius")).doubleValue();
        deltaTau = ((Number) jsonFileObject.get("deltaTau")).doubleValue();
        kValue = ((Number) jsonFileObject.get("kValue")).doubleValue();
        heatCapacity = ((Number) jsonFileObject.get("heatCapacity")).doubleValue();
        maxTau = ((Number) jsonFileObject.get("maxTau")).doubleValue();

        shapeFunctions[0][0] = 0.5 * (1 - TwoPointsGaussianIntegral.point[0]);
        shapeFunctions[0][1] = 0.5 * (1 - TwoPointsGaussianIntegral.point[1]);
        shapeFunctions[1][0] = 0.5 * (1 + TwoPointsGaussianIntegral.point[0]);
        shapeFunctions[1][1] = 0.5 * (1 + TwoPointsGaussianIntegral.point[1]);

    }

    public void generateLocalCoefficientMatrix() {

        for (int i = 0; i < elementQuantity; i++) {

            double hMatrix[][] = new double[2][2];

            double radiusSum = 0.0;
            double ksum11 = 0.0;
            double ksum = 0.0;
            double ksum22 = 0.0;

            for (double[] row : hMatrix) {
                Arrays.fill(row, 0.0);
            }


            for (int j = 0; j < 2; j++) {

                radiusSum += (shapeFunctions[0][j] * nodes[elements[i].getFirstID()].getPositionX() + shapeFunctions[1][j] * nodes[elements[i].getSecondID()].getPositionX()) //rp
                        * TwoPointsGaussianIntegral.weight[j]; //wp

                ksum11 += (shapeFunctions[0][j] * shapeFunctions[0][j]) //Ni*Ni
                        * (shapeFunctions[0][j] * nodes[elements[i].getFirstID()].getPositionX() + shapeFunctions[1][j] * nodes[elements[i].getSecondID()].getPositionX()) //rp
                        * TwoPointsGaussianIntegral.weight[j]; //wp

                ksum += (shapeFunctions[0][j] * shapeFunctions[1][j]) //Ni*Nj
                        * (shapeFunctions[0][j] * nodes[elements[i].getFirstID()].getPositionX() + shapeFunctions[1][j] * nodes[elements[i].getSecondID()].getPositionX()) //rp
                        * TwoPointsGaussianIntegral.weight[j]; //wp

                ksum22 += (shapeFunctions[1][j] * shapeFunctions[1][j]) //Nj*Nj
                        * (shapeFunctions[0][j] * nodes[elements[i].getFirstID()].getPositionX() + shapeFunctions[1][j] * nodes[elements[i].getSecondID()].getPositionX()) //rp
                        * TwoPointsGaussianIntegral.weight[j]; //wp
            }

            hMatrix[0][0] = (elements[i].getkValue() * radiusSum) / deltaRadius + (heatCapacity * materialDensity * deltaRadius * ksum11) / deltaTau;
            hMatrix[0][1] = -(elements[i].getkValue() * radiusSum) / deltaRadius + (heatCapacity * materialDensity * deltaRadius * ksum) / deltaTau;
            hMatrix[1][0] = hMatrix[0][1];
            hMatrix[1][1] = (elements[i].getkValue() * radiusSum) / deltaRadius + (heatCapacity * materialDensity * deltaRadius * ksum22) / deltaTau;

            if (nodes[elements[i].getFirstID()].getBoundaryConditions() == BoundaryConditions.CONVECTION) {
                hMatrix[0][0] += (2 * alpha * maxRadius);
            }

            if (nodes[elements[i].getSecondID()].getBoundaryConditions() == BoundaryConditions.CONVECTION) {
                hMatrix[1][1] += (2 * alpha * maxRadius);
            }

            elements[i].setLocalHMatrix(hMatrix);

        }

    }

    @Override
    public void generateLocalVector() {

        for (int i = 0; i < elementQuantity; i++) {

            double pMatrix[] = new double[2];

            double fsum1 = 0.0;
            double fsum2 = 0.0;

            Arrays.fill(pMatrix, 0.0);

            for (int j = 0; j < 2; j++) {

                fsum1 += (shapeFunctions[0][j] * nodes[elements[i].getFirstID()].getTemperature() + shapeFunctions[1][j] * nodes[elements[i].getSecondID()].getTemperature()) //(Ni*Ti0 + Nj*Tj0)
                        * (shapeFunctions[0][j] * nodes[elements[i].getFirstID()].getPositionX() + shapeFunctions[1][j] * nodes[elements[i].getSecondID()].getPositionX()) //rp
                        * TwoPointsGaussianIntegral.weight[j] //wp
                        * shapeFunctions[0][j]; //Ni

                fsum2 += (shapeFunctions[0][j] * nodes[elements[i].getFirstID()].getTemperature() + shapeFunctions[1][j] * nodes[elements[i].getSecondID()].getTemperature()) //(Ni*Ti0 + Nj*Tj0)
                        * (shapeFunctions[0][j] * nodes[elements[i].getFirstID()].getPositionX() + shapeFunctions[1][j] * nodes[elements[i].getSecondID()].getPositionX()) //rp
                        * TwoPointsGaussianIntegral.weight[j] //wp
                        * shapeFunctions[1][j]; //Nj
            }

            pMatrix[0] = (heatCapacity * materialDensity * deltaRadius * fsum1) / deltaTau;
            pMatrix[1] = (heatCapacity * materialDensity * deltaRadius * fsum2) / deltaTau;

            if (nodes[elements[i].getFirstID()].getBoundaryConditions() == BoundaryConditions.CONVECTION) {
                pMatrix[0] += (2 * alpha * maxRadius * environmentTemperature);
            }

            if (nodes[elements[i].getSecondID()].getBoundaryConditions() == BoundaryConditions.CONVECTION) {
                pMatrix[1] += (2 * alpha * maxRadius * environmentTemperature);
            }

            elements[i].setLocalPMatrix(pMatrix);

        }

    }

    public void generateGlobalCoefficientMatrix() {

        for (int i = 0; i < elementQuantity; i++) {

            int x = elements[i].getFirstID();
            int y = elements[i].getSecondID();

            globalCoefficientMatrix[x][x] += elements[i].getLocalHMatrix()[0][0];
            globalCoefficientMatrix[x][y] += elements[i].getLocalHMatrix()[0][1];
            globalCoefficientMatrix[y][x] += elements[i].getLocalHMatrix()[1][0];
            globalCoefficientMatrix[y][y] += elements[i].getLocalHMatrix()[1][1];

        }

    }

    @Override
    public void generateGlobalVector() {

        Arrays.fill(globalVector, 0.0);

        for (int i = 0; i < elementQuantity; i++) {

            int x = elements[i].getFirstID();
            int y = elements[i].getSecondID();

            globalVector[x] += elements[i].getLocalPMatrix()[0];
            globalVector[y] += elements[i].getLocalPMatrix()[1];

        }

    }

    public void solve() {

        RealMatrix coefficients = new Array2DRowRealMatrix(globalCoefficientMatrix, false);
        DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
        RealVector constatnts = new ArrayRealVector(globalVector, false);
        RealVector solution = solver.solve(constatnts);
        Double[] temperatures = new Double[nodeQuantity];

        for (int j = 0; j < nodeQuantity; j++) {

            temperatures[j] = solution.getEntry(j);
            nodes[j].setTemperature(temperatures[j]);

        }

        this.temperatures.add(temperatures);

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

    public double getAlpha() {
        return alpha;
    }

    public double getEnvironmentTemperature() {
        return environmentTemperature;
    }

    public double[][] getGlobalCoefficientMatrix() {
        return globalCoefficientMatrix;
    }

    public ArrayList<Double> getTau() {
        return tau;
    }

    public int getIndex() {
        return index;
    }

    public ArrayList<Double[]> getTemperatures() {
        return temperatures;
    }

    public double getStartingTemperature() {
        return startingTemperature;
    }
}
