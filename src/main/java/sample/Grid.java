package sample;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Iterator;

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

        try{

            Object object = parser.parse(new FileReader("test.json"));

            JSONObject jsonObject = (JSONObject) object;
            JSONArray jsonArray = (JSONArray) jsonObject.get("DataSets");

            for(int i = 0; i < jsonArray.size(); i++) {

                jsonObject = (JSONObject) jsonArray.get(i);
                elementQuantity += (Integer) jsonObject.get("elementsQuantity");

            }

            elements = new Element[elementQuantity];

            for(int i = 0; i < jsonArray.size(); i++) {

                jsonObject = (JSONObject) jsonArray.get(i);
                Integer elementQuantity = (Integer) jsonObject.get("elementsQuantity");

                for(int j = i; j < i + elementQuantity; j++) {
                    elements[j].setArea( (Double) jsonObject.get("area") );
                    elements[j].setkValue( (Double) jsonObject.get("kValue") );
                }

            }



        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void generateLocalCoefficientMatrix() {



    }

    public void generateGlobalCoefficientMatrix() {



    }

}
