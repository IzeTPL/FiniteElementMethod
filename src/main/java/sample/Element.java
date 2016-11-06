package sample;

/**
 * Created by marian on 30.10.16.
 */
public class Element {

    private int firstID;
    private int secondID;
    private double area;
    private double length;
    private double kValue;
    private double localHMatrix[][];
    private double localPMatrix[];

    public Element() {

        localHMatrix = new double[2][2];
        localPMatrix = new double[2];

    }

    public void setArea(double area) {
        this.area = area;
    }

    public void setkValue(double kValue) {
        this.kValue = kValue;
    }

    public void setFirstID(int firstID) {
        this.firstID = firstID;
    }

    public void setSecondID(int secondID) {
        this.secondID = secondID;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public int getFirstID() {
        return firstID;
    }

    public int getSecondID() {
        return secondID;
    }

    public double getArea() {
        return area;
    }

    public double getLength() {
        return length;
    }

    public double getkValue() {
        return kValue;
    }

    public double[][] getLocalHMatrix() {
        return localHMatrix;
    }

    public void setLocalHMatrix(double[][] localHMatrix) {
        this.localHMatrix = localHMatrix;
    }

    public double[] getLocalPMatrix() {
        return localPMatrix;
    }

    public void setLocalPMatrix(double[] localPMatrix) {
        this.localPMatrix = localPMatrix;
    }
}
