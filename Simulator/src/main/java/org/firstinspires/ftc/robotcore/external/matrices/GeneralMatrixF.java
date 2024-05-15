//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.matrices;

public class GeneralMatrixF extends RowMajorMatrixF {
    float[] data;

    public GeneralMatrixF(int numRows, int numCols) {
        super(numRows, numCols);
        this.data = new float[numRows * numCols];
    }

    private GeneralMatrixF(int numRows, int numCols, int flag) {
        super(numRows, numCols);
    }

    public GeneralMatrixF(int numRows, int numCols, float[] data) {
        super(numRows, numCols);
        if (data.length != numRows * numCols) {
            throw dimensionsError(numRows, numCols);
        } else {
            this.data = data;
        }
    }

    public float[] getData() {
        return this.data;
    }

    public MatrixF emptyMatrix(int numRows, int numCols) {
        return new GeneralMatrixF(numRows, numCols);
    }

    public GeneralMatrixF transposed() {
        return (GeneralMatrixF)super.transposed();
    }
}
