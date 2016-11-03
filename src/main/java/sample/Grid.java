package sample;

import org.json.simple.parser.JSONParser;

/**
 * Created by marian on 30.10.16.
 */

public class Grid {

    private Integer nodeQuantity;
    private Integer elementQuantity;
    private Element[] elements;
    private Node[] nodes;
    private Double heatFluxDensity;
    private Double alpha;
    private Double environmentTemperature;
    private Double globalCoefficientMatrix[][];
    private Double globalVector[];
    private Double temperatures[];

    private JSONParser parser;

    public Grid() {
        parser = new JSONParser();
    }

    public void generateGrid() {



    }

    public void generateLocalCoefficientMatrix() {



    }

    public void generateGlobalCoefficientMatrix() {



    }

}
