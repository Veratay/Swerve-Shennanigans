//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.matrices;

public class SliceMatrixF extends MatrixF {
    protected MatrixF matrix;
    protected int row;
    protected int col;

    public SliceMatrixF(MatrixF matrix, int row, int col, int numRows, int numCols) {
        super(numRows, numCols);
        this.matrix = matrix;
        this.row = row;
        this.col = col;
        if (row + numRows >= matrix.numRows) {
            throw this.dimensionsError();
        } else if (col + numCols >= matrix.numCols) {
            throw this.dimensionsError();
        }
    }

    public float get(int row, int col) {
        return this.matrix.get(this.row + row, this.col + col);
    }

    public void put(int row, int col, float value) {
        this.matrix.put(this.row + row, this.col + col, value);
    }

    public MatrixF emptyMatrix(int numRows, int numCols) {
        return this.matrix.emptyMatrix(numRows, numCols);
    }
}
