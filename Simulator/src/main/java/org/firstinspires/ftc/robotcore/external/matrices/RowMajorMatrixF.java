//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.matrices;

public abstract class RowMajorMatrixF extends DenseMatrixF {
    public RowMajorMatrixF(int nRows, int nCols) {
        super(nRows, nCols);
    }

    protected int indexFromRowCol(int row, int col) {
        return row * this.numCols + col;
    }

    public VectorF toVector() {
        return new VectorF(this.getData());
    }
}
