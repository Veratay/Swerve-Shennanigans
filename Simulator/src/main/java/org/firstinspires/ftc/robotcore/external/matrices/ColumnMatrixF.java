//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.matrices;

public class ColumnMatrixF extends MatrixF {
    VectorF vector;

    public ColumnMatrixF(VectorF vector) {
        super(vector.length(), 1);
        this.vector = vector;
    }

    public float get(int row, int col) {
        return this.vector.get(row);
    }

    public void put(int row, int col, float value) {
        this.vector.put(row, value);
    }

    public MatrixF emptyMatrix(int numRows, int numCols) {
        return new GeneralMatrixF(numRows, numCols);
    }
}
