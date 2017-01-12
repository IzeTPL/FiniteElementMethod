package model;
/**
 * Created by Marian on 06.01.2017.
 */
public interface IGrid {

    void generateGrid();
    void init();
    void readElements();
    void readBoundaryConditions();
    void readParameters();
    void generateLocalCoefficientMatrix();
    void generateLocalVector();
    void generateGlobalCoefficientMatrix();
    void generateGlobalVector();
    void solve();
    void save();

}
