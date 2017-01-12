package fem;

/**
 * Created by marian on 30.10.16.
 */
public class Node {

    private double temperature;
    private double positionX;
    private BoundaryConditions boundaryConditions = BoundaryConditions.NULL;


    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public void setBoundaryConditions(BoundaryConditions boundaryConditions) {
        this.boundaryConditions = boundaryConditions;
    }


    public double getPositionX() {
        return positionX;
    }

    public BoundaryConditions getBoundaryConditions() {
        return boundaryConditions;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
