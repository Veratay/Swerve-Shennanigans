//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.matrices;

public abstract class DenseMatrixF extends MatrixF {
    protected DenseMatrixF(int nRows, int nCols) {
        super(nRows, nCols);
    }

    public float get(int row, int col) {
        return this.getData()[this.indexFromRowCol(row, col)];
    }

    public void put(int row, int col, float value) {
        this.getData()[this.indexFromRowCol(row, col)] = value;
    }

    public abstract float[] getData();

    protected abstract int indexFromRowCol(int var1, int var2);
}
