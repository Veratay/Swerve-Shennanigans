//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.matrices;

public class RowMatrixF extends MatrixF {
    VectorF vector;

    public RowMatrixF(VectorF vector) {
        super(1, vector.length());
        this.vector = vector;
    }

    public float get(int row, int col) {
        return this.vector.get(col);
    }

    public void put(int row, int col, float value) {
        this.vector.put(col, value);
    }

    public MatrixF emptyMatrix(int numRows, int numCols) {
        return new GeneralMatrixF(numRows, numCols);
    }
}
