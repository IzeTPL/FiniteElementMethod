package sample;

/**
 * Created by marian on 30.10.16.
 */
public class Element {

    private Integer firstID;
    private Integer secondID;
    private Double area;
    private Double length;
    private Double kValue;
    private Double localHMatrix;
    private Double localPMatrix;

    public Integer getFirstID() {
        return firstID;
    }

    public void setFirstID(Integer firstID) {
        this.firstID = firstID;
    }

    public Integer getSecondID() {
        return secondID;
    }

    public void setSecondID(Integer secondID) {
        this.secondID = secondID;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getkValue() {
        return kValue;
    }

    public void setkValue(Double kValue) {
        this.kValue = kValue;
    }

    public Double getLocalHMatrix() {
        return localHMatrix;
    }

    public void setLocalHMatrix(Double localHMatrix) {
        this.localHMatrix = localHMatrix;
    }

    public Double getLocalPMatrix() {
        return localPMatrix;
    }

    public void setLocalPMatrix(Double localPMatrix) {
        this.localPMatrix = localPMatrix;
    }
}
