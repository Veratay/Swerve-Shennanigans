//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.matrices;

public abstract class ColumnMajorMatrixF extends DenseMatrixF {
    public ColumnMajorMatrixF(int nRows, int nCols) {
        super(nRows, nCols);
    }

    protected int indexFromRowCol(int row, int col) {
        return col * this.numRows + row;
    }

    public VectorF toVector() {
        return new VectorF(this.getData());
    }
}
